package com.rbkmoney.magista.event;

import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.EventSource;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InvoicingKafkaListener {

    private final List<Handler> handlers;
    private final MachineEventParser<EventPayload> parser;

    @KafkaListener(topics = "${kafka.topics.invoice.id}", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SinkEvent sinkEvent, Acknowledgment ack) {
        log.debug("Reading sinkEvent, sourceId: {}, sequenceId: {}", sinkEvent.getEvent().getSourceId(), sinkEvent.getEvent().getEventId());
        MachineEvent machineEvent = sinkEvent.getEvent();
        EventPayload payload = parser.parse(machineEvent);
        Event event = new Event()
                .setId(machineEvent.getEventId())
                .setCreatedAt(machineEvent.getCreatedAt())
                .setPayload(payload)
                .setSource(EventSource.invoice_id(machineEvent.getSourceId()));

        if (payload.isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : payload.getInvoiceChanges()) {
                Handler handler = getHandler(invoiceChange);
                if (handler != null) {
                    handler.handle(
                            invoiceChange,
                            new StockEvent()
                                    .setId(machineEvent.getEventId())
                                    .setTime(machineEvent.getCreatedAt())
                                    .setSourceEvent(
                                            SourceEvent.processing_event(event)
                                    )
                    ).execute();
                }
            }
            ack.acknowledge();
        }
    }

    protected <C> Handler getHandler(C change) {
        for (Handler handler : handlers) {
            if (handler.accept(change)) {
                return handler;
            }
        }
        return null;
    }
}


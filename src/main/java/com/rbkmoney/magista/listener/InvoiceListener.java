package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.magista.event.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InvoiceListener extends AbstractListener {

    public InvoiceListener(List<Handler> handlers) {
        super(handlers);
    }

    @KafkaListener(topics = "${kafka.invoice.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload byte[] message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long key) {
        StockEvent stockEvent = Geck.msgPackToTBase(message, StockEvent.class);
        EventPayload payload = stockEvent.getSourceEvent().getProcessingEvent().getPayload();
        if (payload.isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : payload.getInvoiceChanges()) {
                Handler handler = getHandler(invoiceChange);
                if (handler != null) {
                    log.info("Start invoice event handling, id='{}', eventType='{}', handlerType='{}'",
                            stockEvent.getSourceEvent().getProcessingEvent().getId(), handler.getChangeType(), handler.getClass().getSimpleName());
                    handler.handle(invoiceChange, stockEvent);
                }
            }
        }
    }
}

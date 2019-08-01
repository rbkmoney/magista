package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.exception.ConsumerException;
import com.rbkmoney.magista.service.HandlerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.util.KafkaUtil.toSummaryString;
import static com.rbkmoney.magista.util.KafkaUtil.toSummaryStringWithValues;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceListener implements MessageListener {

    private final HandlerManager handlerManager;
    private final SourceEventParser eventParser;

    @KafkaListener(topics = "${kafka.topics.invoicing}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, MachineEvent>> messages, Acknowledgment ack) {
        List<MachineEvent> machineEvents = messages.stream()
                .map(message -> message.value())
                .collect(Collectors.toList());

        try {
            handle(machineEvents, ack);
            ack.acknowledge();
            log.info("Records have been committed, size={}, {}", messages.size(), toSummaryStringWithValues(messages));
        } catch (RuntimeException ex) {
            sleepBeforeRetry();
            throw new ConsumerException(String.format("Records commit failed, size=%d, %s", messages.size(), toSummaryString(messages)), ex);
        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void handle(List<MachineEvent> machineEvents, Acknowledgment ack) {
        machineEvents.stream()
                .map(machineEvent -> Map.entry(eventParser.parseEvent(machineEvent), machineEvent))
                .filter(entry -> entry.getKey().isSetInvoiceChanges())
                .map(entry -> {
                            List<Map.Entry<InvoiceChange, MachineEvent>> invoiceChangesWithMachineEvent = new ArrayList<>();
                            for (InvoiceChange invoiceChange : entry.getKey().getInvoiceChanges()) {
                                invoiceChangesWithMachineEvent.add(Map.entry(invoiceChange, entry.getValue()));
                            }
                            return invoiceChangesWithMachineEvent;
                        }
                )
                .flatMap(List::stream)
                .sorted(Comparator.comparingLong(o -> o.getValue().getEventId()))
                .collect(
                        Collectors.groupingBy(
                                entry -> handlerManager.getHandler(entry.getKey()),
                                LinkedHashMap::new,
                                Collectors.toList()
                        )
                )
                .forEach(
                        (handler, invoiceChangesWithMachineEvent) -> {
                            if (handler != null) {
                                handler.handle(invoiceChangesWithMachineEvent).execute();
                            }
                        }
                );
    }
}

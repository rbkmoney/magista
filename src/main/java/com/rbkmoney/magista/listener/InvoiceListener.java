package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.service.HandlerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceListener implements MessageListener {

    private final HandlerManager handlerManager;
    private final SourceEventParser eventParser;

    @KafkaListener(topics = "${kafka.topics.invoicing}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<MachineEvent> messages, Acknowledgment ack) {
        handle(messages, ack);
        ack.acknowledge();
    }

    @Override
    public void handle(List<MachineEvent> machineEvents, Acknowledgment ack) {
        machineEvents.stream()
                .map(machineEvent -> Map.entry(machineEvent, eventParser.parseEvent(machineEvent)))
                .filter(entry -> entry.getValue().isSetInvoiceChanges())
                .map(entry -> {
                            List<Map.Entry<MachineEvent, InvoiceChange>> invoiceChangesWithMachineEvent = new ArrayList<>();
                            for (InvoiceChange invoiceChange : entry.getValue().getInvoiceChanges()) {
                                invoiceChangesWithMachineEvent.add(Map.entry(entry.getKey(), invoiceChange));
                            }
                            return invoiceChangesWithMachineEvent;
                        }
                )
                .flatMap(List::stream)
                .collect(
                        Collectors.groupingBy(
                                entry -> handlerManager.getHandler(entry.getValue()),
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

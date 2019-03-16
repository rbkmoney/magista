package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.config.KafkaConfig;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.listener.InvoiceListener;
import com.rbkmoney.magista.listener.SafeMessageConsumer;
import com.rbkmoney.magista.service.HandlerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ContextConfiguration(classes = {KafkaConfig.class, KafkaAutoConfiguration.class,  InvoiceListener.class})
public class InvoiceListenerKafkaTest extends KafkaAbstractTest {

    @MockBean
    HandlerManager handlerManager;
    @MockBean
    SafeMessageConsumer safeMessageConsumer;
    @MockBean
    SourceEventParser eventParser;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        Producer<String, MachineEvent> producer = createProducer();
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(new InvoiceChange());
        payload.setInvoiceChanges(invoiceChanges);
        event.setPayload(payload);
        sourceEvent.setProcessingEvent(event);
        Value data = new Value();
        data.setBin(new byte[0]);
        message.setData(data);

        ProducerRecord<String, MachineEvent> producerRecord = new ProducerRecord<>(topic,
                null, message);
        try {
            producer.send(producerRecord).get();
        } catch (Exception e) {
            log.error("KafkaAbstractTest initialize e: ", e);
        }
        producer.close();

        Thread.sleep(10000L);
        Mockito.verify(safeMessageConsumer, Mockito.times(1)).safeMessageHandler(any(), any(), any());
    }

}

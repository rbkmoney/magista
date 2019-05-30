package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.config.KafkaConfig;
import com.rbkmoney.magista.config.RetryConfig;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.listener.InvoiceListener;
import com.rbkmoney.magista.service.HandlerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Collections.EMPTY_LIST;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@TestPropertySource(properties = "kafka.ssl.enable=false")
@ContextConfiguration(classes = {KafkaConfig.class, KafkaAutoConfiguration.class, InvoiceListener.class, RetryConfig.class})
public class InvoiceListenerKafkaTest extends KafkaAbstractTest {

    @MockBean
    HandlerManager handlerManager;
    @MockBean
    SourceEventParser eventParser;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
        Producer<String, SinkEvent> producer = createProducer();
        MachineEvent message = new MachineEvent();
        Value data = new Value();
        data.setBin(new byte[0]);
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(SOURCE_ID);
        message.setData(data);
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);
        Mockito.when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_changes(EMPTY_LIST));

        ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(topic,
                null, sinkEvent);
        try {
            producer.send(producerRecord).get();
        } catch (Exception e) {
            log.error("KafkaAbstractTest initialize e: ", e);
        }
        producer.close();

        Thread.sleep(1000L);
        Mockito.verify(eventParser, Mockito.times(1)).parseEvent(any());
    }

}

package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.config.AbstractKafkaConfig;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.service.HandlerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoiceListenerKafkaTest extends AbstractKafkaConfig {

    @MockBean
    private HandlerManager handlerManager;

    @MockBean
    private SourceEventParser eventParser;

    @Autowired
    private KafkaTemplate<String, SinkEvent> transactionKafkaTemplate;

    @Test
    public void listenEmptyChanges() throws InterruptedException {
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
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_changes(List.of()));
        transactionKafkaTemplate.send(invoicingTopic, sinkEvent);
        Thread.sleep(1000L);
        verify(eventParser, times(1)).parseEvent(any());
        reset(eventParser);
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_template_changes(List.of()));
        transactionKafkaTemplate.send(invoiceTemplateTopic, sinkEvent);
        Thread.sleep(1000L);
        verify(eventParser, times(1)).parseEvent(any());
    }
}

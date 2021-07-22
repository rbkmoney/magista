package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.config.AbstractKafkaAndDaoConfig;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.service.HandlerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoicingListenerTest extends AbstractKafkaAndDaoConfig {

    @Value("${kafka.topics.invoicing.id}")
    private String invoicingTopicName;

    @MockBean
    private HandlerManager handlerManager;

    @MockBean
    private SourceEventParser eventParser;

    @Test
    public void shouldInvoicingSinkEventListen() throws InterruptedException {
        var message = new MachineEvent();
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceId("source_id");
        message.setSourceNs("source_ns");
        var data = new com.rbkmoney.machinegun.msgpack.Value();
        data.setBin(new byte[0]);
        message.setData(data);
        var sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_changes(List.of()));
        produce(invoicingTopicName, sinkEvent);
        Thread.sleep(3000L);
        verify(eventParser, times(1)).parseEvent(any());
    }
}

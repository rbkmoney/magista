package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.config.MagistaSpringBootITest;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.service.HandlerManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MagistaSpringBootITest
public class InvoiceTemplateListenerTest {

    @Value("${kafka.topics.invoice-template.id}")
    private String invoiceTemplateTopicName;

    @MockBean
    private HandlerManager handlerManager;

    @MockBean
    private SourceEventParser eventParser;

    @Autowired
    private KafkaTemplate<String, SinkEvent> testSinkEventProducer;

    @Captor
    private ArgumentCaptor<MachineEvent> arg;

    @Test
    public void shouldInvoiceTemplateSinkEventListen() {
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
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_template_changes(List.of()));
        testSinkEventProducer.send(invoiceTemplateTopicName, sinkEvent)
                .completable()
                .join();
        verify(eventParser, timeout(3000).times(1)).parseEvent(arg.capture());
        assertThat(arg.getValue())
                .isEqualTo(message);
    }
}

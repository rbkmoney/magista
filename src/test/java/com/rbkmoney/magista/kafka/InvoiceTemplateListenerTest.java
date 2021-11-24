package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.config.KafkaPostgresqlSpringBootITest;
import com.rbkmoney.magista.converter.SourceEventsParser;
import com.rbkmoney.magista.service.HandlerManager;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@KafkaPostgresqlSpringBootITest
public class InvoiceTemplateListenerTest {

    @Value("${kafka.topics.invoice-template.id}")
    private String invoiceTemplateTopicName;

    @MockBean
    private HandlerManager handlerManager;

    @MockBean
    private SourceEventsParser sourceEventsParser;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

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
        when(sourceEventsParser.parseEvents(any()))
                .thenReturn(List.of(EventPayload.invoice_template_changes(List.of())));
        testThriftKafkaProducer.send(invoiceTemplateTopicName, sinkEvent);
        verify(sourceEventsParser, timeout(5000).times(1)).parseEvents(arg.capture());
        assertThat(arg.getValue())
                .isEqualTo(message);
    }
}

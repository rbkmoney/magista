package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.config.AbstractKafkaConfig;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.service.HandlerManager;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.payout.manager.domain.CurrencyRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaListenerTest extends AbstractKafkaConfig {

    @Value("${kafka.topics.invoicing.id}")
    private String invoicingTopicName;

    @Value("${kafka.topics.invoice-template.id}")
    private String invoiceTemplateTopicName;

    @Value("${kafka.topics.pm-events-payout.id}")
    private String payoutTopicName;

    @MockBean
    private HandlerManager handlerManager;

    @MockBean
    private SourceEventParser eventParser;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[] {handlerManager, eventParser};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

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
        Thread.sleep(1000L);
        verify(eventParser, times(1)).parseEvent(any());
    }

    @Test
    public void shouldInvoiceTemplateSinkEventListen() throws InterruptedException {
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
        produce(invoiceTemplateTopicName, sinkEvent);
        Thread.sleep(1000L);
        verify(eventParser, times(1)).parseEvent(any());
    }

    @Test
    public void shouldPayoutEventListen() throws InterruptedException {
        Event event = new Event();
        event.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        event.setPayoutId("payout_id");
        event.setSequenceId(1);
        Payout payout = new Payout()
                .setPayoutId("payout_id")
                .setPartyId("1")
                .setShopId("SHOP_ID")
                .setStatus(PayoutStatus.paid(new PayoutPaid()))
                .setPayoutToolId("111")
                .setFee(0L)
                .setAmount(10L)
                .setCurrency(new CurrencyRef()
                        .setSymbolicCode("RUB"))
                .setCashFlow(Collections.emptyList())
                .setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        payout.setPayoutId("payout_id");
        event.setPayoutChange(PayoutChange.created(new PayoutCreated(payout)));
        event.setPayout(payout);
        producePayout(payoutTopicName, event);
        Thread.sleep(1000L);
    }
}

package com.rbkmoney.magista.kafka;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.config.KafkaPostgresqlSpringBootITest;
import com.rbkmoney.magista.service.PayoutHandlerService;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.payout.manager.domain.CurrencyRef;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@KafkaPostgresqlSpringBootITest
public class PayoutListenerTest {

    @Value("${kafka.topics.pm-events-payout.id}")
    private String payoutTopicName;

    @MockBean
    private PayoutHandlerService payoutService;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @Captor
    private ArgumentCaptor<List<Event>> arg;

    @Test
    public void shouldPayoutEventListen() {
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
        testThriftKafkaProducer.send(payoutTopicName, event);
        verify(payoutService, timeout(5000).times(1)).handleEvents(arg.capture());
        assertThat(arg.getValue().get(0))
                .isEqualTo(event);
    }
}

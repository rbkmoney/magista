package com.rbkmoney.magista.kafka;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.config.AbstractKafkaConfig;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.payout.manager.domain.CurrencyRef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class PayoutListenerKafkaTest extends AbstractKafkaConfig {

    @Value("${kafka.topics.pm-events-payout.id}")
    private String topicName;

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
        producePayout(topicName, event);
        Thread.sleep(1000L);
    }
}

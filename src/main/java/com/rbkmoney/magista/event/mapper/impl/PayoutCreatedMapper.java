package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.payout.manager.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.mapper.PayoutMapper;
import com.rbkmoney.magista.service.PayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayoutCreatedMapper implements PayoutMapper {

    private final PayoutService payoutEventService;

    @Autowired
    public PayoutCreatedMapper(PayoutService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    public Processor map(PayoutChange change, Event event) {
        var payout = new com.rbkmoney.magista.domain.tables.pojos.Payout();
        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setPayoutId(event.getPayoutId());
        payout.setSequenceId(event.getSequenceId());

        Payout payoutSource = change.getCreated().getPayout();
        payout.setStatus(TBaseUtil.unionFieldToEnum(payoutSource.getStatus(), PayoutStatus.class));
        payout.setCreatedAt(TypeUtil.stringToLocalDateTime(payoutSource.getCreatedAt()));

        payout.setPayoutToolId(payoutSource.getPayoutToolId());
        payout.setAmount(payoutSource.getAmount());
        payout.setFee(payoutSource.getFee());
        payout.setCurrencyCode(payoutSource.getCurrency().getSymbolicCode());

        payout.setPartyId(payoutSource.getPartyId());
        payout.setShopId(payoutSource.getShopId());
        return () -> payoutEventService.savePayout(payout);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_CREATED;
    }

    @Override
    public boolean accept(PayoutChange payoutChange) {
        return payoutChange.isSetCreated();
    }
}

package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.tables.pojos.Payout;
import com.rbkmoney.magista.event.handler.PayoutHandler;
import com.rbkmoney.magista.service.PayoutService;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import com.rbkmoney.payout.manager.PayoutStatusChanged;
import org.springframework.stereotype.Component;

@Component
public class PayoutStatusChangedHandler implements PayoutHandler {

    private final PayoutService payoutEventService;

    public PayoutStatusChangedHandler(PayoutService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    public void handle(PayoutChange change, Event event) {
        Payout payout = payoutEventService.getPayout(event.getPayoutId());

        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setSequenceId(event.getSequenceId());

        PayoutStatusChanged statusChanged = change.getStatusChanged();
        payout.setStatus(TBaseUtil.unionFieldToEnum(statusChanged.getStatus(), PayoutStatus.class));

        if (statusChanged.getStatus().isSetCancelled()) {
            payout.setCancelledDetails(statusChanged.getStatus().getCancelled().getDetails());
        }
        payoutEventService.updatePayout(payout);
    }

    @Override
    public boolean accept(PayoutChange change) {
        return change.isSetStatusChanged();
    }
}

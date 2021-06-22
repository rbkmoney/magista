package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.tables.pojos.Payout;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.mapper.PayoutMapper;
import com.rbkmoney.magista.service.PayoutService;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import com.rbkmoney.payout.manager.PayoutStatusChanged;
import org.springframework.stereotype.Component;

@Component
public class PayoutStatusChangedMapper implements PayoutMapper {

    private final PayoutService payoutEventService;

    public PayoutStatusChangedMapper(PayoutService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    public Processor map(PayoutChange change, Event event) {
        Payout payout = new Payout();

        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setPayoutId(event.getSource().getId());

        if (change.isSetStatusChanged()) {
            PayoutStatusChanged statusChanged = change.getStatusChanged();
            payout.setStatus(TBaseUtil.unionFieldToEnum(statusChanged.getStatus(), PayoutStatus.class));

            if (statusChanged.getStatus().isSetCancelled()) {
                payout.setCancelledDetails(statusChanged.getStatus().getCancelled().getDetails());
            }
        }
        return () -> payoutEventService.savePayoutChange(payout);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_STATUS_CHANGED;
    }

}

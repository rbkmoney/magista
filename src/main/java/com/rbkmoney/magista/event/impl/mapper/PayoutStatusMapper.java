package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;

public class PayoutStatusMapper implements Mapper<PayoutEventContext> {
    @Override
    public PayoutEventContext fill(PayoutEventContext context) {
        Event event = context.getSource();
        PayoutEventStat payoutEventStat = context.getPayoutEventStat();

        payoutEventStat.setEventId(event.getId());
        payoutEventStat.setEventCreatedAt(
                TypeUtil.stringToLocalDateTime(event.getCreatedAt())
        );
        payoutEventStat.setPayoutId(event.getSource().getPayoutId());

        PayoutChange change = context.getPayoutChange();
        if (change.isSetPayoutStatusChanged()) {
            payoutEventStat.setEventCategory(PayoutEventCategory.PAYOUT);
            payoutEventStat.setEventType(PayoutEventType.PAYOUT_STATUS_CHANGED);

            PayoutStatusChanged statusChanged = change.getPayoutStatusChanged();

            payoutEventStat.setPayoutStatus(
                    TBaseUtil.unionFieldToEnum(statusChanged.getStatus(), PayoutStatus.class)
            );

            if (statusChanged.getStatus().isSetCancelled()) {
                payoutEventStat.setPayoutCancelDetails(
                        statusChanged.getStatus().getCancelled().getDetails()
                );
            }
        }

        return context.setPayoutEventStat(payoutEventStat);
    }
}

package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.damsel.payout_processing.PayoutStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PayoutMapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PayoutService;
import org.springframework.stereotype.Component;

@Component
public class PayoutStatusChangedMapper implements PayoutMapper {

    private final PayoutService payoutEventService;

    public PayoutStatusChangedMapper(PayoutService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    public Processor map(PayoutChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getPayoutEvent();
        PayoutData payoutData = new PayoutData();

        payoutData.setEventId(event.getId());
        payoutData.setEventCreatedAt(
                TypeUtil.stringToLocalDateTime(event.getCreatedAt())
        );
        payoutData.setPayoutId(event.getSource().getPayoutId());

        if (change.isSetPayoutStatusChanged()) {
            payoutData.setEventType(PayoutEventType.PAYOUT_STATUS_CHANGED);

            PayoutStatusChanged statusChanged = change.getPayoutStatusChanged();

            payoutData.setPayoutStatus(
                    TBaseUtil.unionFieldToEnum(statusChanged.getStatus(), PayoutStatus.class)
            );

            if (statusChanged.getStatus().isSetCancelled()) {
                payoutData.setPayoutCancelDetails(
                        statusChanged.getStatus().getCancelled().getDetails()
                );
            }
        }
        return () -> payoutEventService.savePayout(payoutData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_STATUS_CHANGED;
    }

}

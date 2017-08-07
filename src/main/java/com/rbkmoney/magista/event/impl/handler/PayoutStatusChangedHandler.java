package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;
import com.rbkmoney.magista.event.impl.mapper.PayoutStatusMapper;
import com.rbkmoney.magista.service.PayoutEventService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class PayoutStatusChangedHandler extends AbstractPayoutEventHandler {

    private final PayoutEventService payoutEventService;

    @Autowired
    public PayoutStatusChangedHandler(PayoutEventService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new PayoutStatusMapper()
        );
    }

    @Override
    public Processor handle(PayoutChange change, StockEvent parent) {
        PayoutEventContext context = generateContext(change, parent);
        return () -> payoutEventService.changePayoutEventStatus(context.getPayoutEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_STATUS_CHANGED;
    }
}

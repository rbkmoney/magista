package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;
import com.rbkmoney.magista.event.impl.mapper.PayoutMapper;
import com.rbkmoney.magista.service.PayoutEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PayoutCreatedHandler extends AbstractPayoutEventHandler {

    private final PayoutEventService payoutEventService;

    @Autowired
    public PayoutCreatedHandler(PayoutEventService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new PayoutMapper()
        );
    }

    @Override
    public Processor handle(PayoutChange change, Event parent) {
        PayoutEventContext context = generateContext(change, parent);
        return () -> payoutEventService.savePayoutEvent(context.getPayoutEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_CREATED;
    }
}

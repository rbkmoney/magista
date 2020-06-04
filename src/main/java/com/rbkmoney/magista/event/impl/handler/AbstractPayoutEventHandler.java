package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;

import java.util.List;

public abstract class AbstractPayoutEventHandler implements Handler<PayoutChange, Event> {

    public PayoutEventContext generateContext(PayoutChange change, Event event) {
        PayoutEventContext context = new PayoutEventContext(change, event);
        for (Mapper mapper : getMappers()) {
            context = (PayoutEventContext) mapper.fill(context);
        }
        return context;
    }

    abstract List<Mapper> getMappers();
}

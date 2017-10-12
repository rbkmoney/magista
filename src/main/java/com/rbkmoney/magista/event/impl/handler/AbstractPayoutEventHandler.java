package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;

import java.util.List;

public abstract class AbstractPayoutEventHandler implements Handler<PayoutChange, StockEvent> {

    public PayoutEventContext generateContext(PayoutChange change, StockEvent event) {
        PayoutEventContext context = new PayoutEventContext(change, event);
        for (Mapper mapper : getMappers()) {
            context = (PayoutEventContext) mapper.fill(context);
        }
        return context;
    }

    abstract List<Mapper> getMappers();
}

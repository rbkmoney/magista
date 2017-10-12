package com.rbkmoney.magista.event.impl.context;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.event.EventContext;

public class PayoutEventContext implements EventContext<StockEvent> {
    private final PayoutChange payoutChange;
    private final StockEvent stockEvent;
    private PayoutEventStat payoutEventStat;

    public PayoutEventContext(PayoutChange payoutChange, StockEvent stockEvent) {
        this.payoutChange = payoutChange;
        this.stockEvent = stockEvent;
        this.payoutEventStat = new PayoutEventStat();
    }

    @Override
    public StockEvent getSource() {
        return stockEvent;
    }

    public PayoutChange getPayoutChange() {
        return payoutChange;
    }

    public PayoutEventStat getPayoutEventStat() {
        return payoutEventStat;
    }

    public PayoutEventContext setPayoutEventStat(PayoutEventStat payoutEventStat) {
        this.payoutEventStat = payoutEventStat;
        return this;
    }
}

package com.rbkmoney.magista.event.impl.context;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.event.EventContext;

public class PayoutEventContext implements EventContext<Event> {
    private final PayoutChange payoutChange;
    private final Event event;
    private PayoutEventStat payoutEventStat;

    public PayoutEventContext(PayoutChange payoutChange, Event event) {
        this.payoutChange = payoutChange;
        this.event = event;
        this.payoutEventStat = new PayoutEventStat();
    }

    @Override
    public Event getSource() {
        return event;
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

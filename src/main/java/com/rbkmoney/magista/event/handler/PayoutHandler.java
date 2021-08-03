package com.rbkmoney.magista.event.handler;

import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;

public interface PayoutHandler extends Handler<PayoutChange, Event> {
}

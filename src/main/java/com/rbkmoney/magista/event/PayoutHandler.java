package com.rbkmoney.magista.event;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.PayoutChange;

public interface PayoutHandler extends Handler<PayoutChange, StockEvent, Processor> {
}

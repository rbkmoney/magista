package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.Processor;

public interface PayoutMapper extends Mapper<PayoutChange, StockEvent, Processor> {
}

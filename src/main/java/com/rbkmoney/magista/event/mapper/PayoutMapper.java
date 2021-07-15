package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import com.rbkmoney.magista.event.Processor;

public interface PayoutMapper extends Mapper<PayoutChange, Event, Processor> {
}

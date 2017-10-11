package com.rbkmoney.magista.event.impl.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.service.ProcessingService;

public class PayoutEventStockHandler implements EventHandler<StockEvent> {

    private final ProcessingService processingService;

    public PayoutEventStockHandler(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    public EventAction handle(StockEvent stockEvent, String s) {
        processingService.processPayoutEvent(stockEvent);
        return EventAction.CONTINUE;
    }
}

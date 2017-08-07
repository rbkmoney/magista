package com.rbkmoney.magista.event.impl.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.service.ProcessingService;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceEventStockHandler implements EventHandler<StockEvent> {

    private final ProcessingService processingService;

    public InvoiceEventStockHandler(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    public EventAction handle(StockEvent event, String subsKey) {
        processingService.processInvoiceEvent(event);
        return EventAction.CONTINUE;
    }
}

package com.rbkmoney.magista.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class EventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    EventService eventService;

    public EventStockHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handleEvent(StockEvent stockEvent, String subsKey) {
        eventService.processEvent(stockEvent);
    }

    @Override
    public void handleNoMoreElements(String subsKey) {
        log.warn("No more elements in BM, Subs key '{}'", subsKey);
    }
}

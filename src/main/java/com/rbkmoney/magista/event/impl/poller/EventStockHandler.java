package com.rbkmoney.magista.event.impl.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class EventStockHandler implements EventHandler<StockEvent> {

    private EventService eventService;

    public EventStockHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public EventAction handle(StockEvent event, String subsKey) {
        eventService.processEvent(event);
        return EventAction.CONTINUE;
    }
}

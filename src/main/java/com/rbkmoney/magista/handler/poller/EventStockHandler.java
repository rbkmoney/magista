package com.rbkmoney.magista.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class EventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    List<Handler> handlers;

    public EventStockHandler(List<Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handleEvent(StockEvent stockEvent, String subsKey) {
        for (Handler handler : handlers) {
            if (handler.accept(stockEvent)) {
                handler.handle(stockEvent);
                break;
            }
        }
    }

    @Override
    public void handleNoMoreElements(String subsKey) {
        log.warn("No more elements in BM, Subs key '{}'", subsKey);
    }
}

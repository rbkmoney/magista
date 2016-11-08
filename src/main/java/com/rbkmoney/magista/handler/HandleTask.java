package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.poll.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class HandleTask implements Callable<Pair> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private StockEvent stockEvent;
    private List<Handler> handlers;

    public HandleTask(StockEvent stockEvent, List<Handler> handlers) {
        this.stockEvent = stockEvent;
        this.handlers = handlers;
    }

    @Override
    public Pair call() throws Exception {
        long eventId = stockEvent.getSourceEvent().getProcessingEvent().getId();
        log.info("Start event handling, eventId='{}'", eventId);

        for (Handler handler : handlers) {
            if (handler.accept(stockEvent)) {
                return new Pair<>(eventId, handler.handle(stockEvent));
            }
        }
        return new Pair<>(eventId, null);
    }
}

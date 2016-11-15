package com.rbkmoney.magista.event;

import com.rbkmoney.damsel.event_stock.StockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class HandleTask implements Callable<EventContext> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private StockEvent stockEvent;
    private Handler handler;

    public HandleTask(StockEvent stockEvent, Handler handler) {
        this.stockEvent = stockEvent;
        this.handler = handler;
    }

    @Override
    public EventContext call() throws Exception {
        log.info("Start event handling, id='{}', type='{}'",
                stockEvent.getSourceEvent().getProcessingEvent().getId(), handler.getEventType());

        return handler.handle(stockEvent);
    }
}

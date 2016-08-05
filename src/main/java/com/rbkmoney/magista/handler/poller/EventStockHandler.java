package com.rbkmoney.magista.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.handler.Handler;
import com.rbkmoney.magista.handler.InvoiceCreatedHandler;
import com.rbkmoney.magista.handler.InvoiceStatusChangedHandler;
import com.rbkmoney.magista.handler.PaymentStartedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class EventStockHandler implements EventHandler<StockEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    List<Handler> handlers = Arrays.asList(new Handler[]{
            new InvoiceCreatedHandler(),
            new InvoiceStatusChangedHandler(),
            new PaymentStartedHandler()
    });

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

    }
}

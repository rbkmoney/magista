package com.rbkmoney.magista.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.magista.handler.Handler;
import com.rbkmoney.magista.handler.InvoiceCreatedHandler;
import com.rbkmoney.magista.handler.InvoiceStatusChangedHandler;
import com.rbkmoney.magista.handler.PaymentStartedHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class EventStockHandler implements EventHandler<StockEvent> {

    List<Handler> handlers = Arrays.asList(new Handler[]{
            new InvoiceCreatedHandler(),
            new InvoiceStatusChangedHandler(),
            new PaymentStartedHandler()
    });

    @Override
    public void handleEvent(StockEvent stockEvent, String subsKey) {
        handlers.stream().filter(handler -> handler.accept(stockEvent)).findFirst().get().handle(stockEvent);
    }

    @Override
    public void handleNoMoreElements(String subsKey) {

    }
}

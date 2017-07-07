package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

import java.util.List;

/**
 * Created by tolkonepiu on 14/11/2016.
 */
public abstract class AbstractInvoiceEventHandler implements Handler<InvoiceChange, StockEvent> {

    public InvoiceEventContext generateContext(InvoiceChange change, StockEvent event) {
        InvoiceEventContext context = new InvoiceEventContext(change, event);
        for (Mapper mapper : getMappers()) {
            context = (InvoiceEventContext) mapper.fill(context);
        }

        return context;
    }

    abstract List<Mapper> getMappers();

}

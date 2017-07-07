package com.rbkmoney.magista.event.impl.context;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.EventContext;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceEventContext implements EventContext<StockEvent> {

    private StockEvent stockEvent;
    private InvoiceEventStat invoiceEventStat;

    public InvoiceEventContext(StockEvent stockEvent) {
        this.stockEvent = stockEvent;
        this.invoiceEventStat = new InvoiceEventStat();
    }

    @Override
    public StockEvent getSource() {
        return stockEvent;
    }

    public InvoiceEventStat getInvoiceEventStat() {
        return invoiceEventStat;
    }

    public InvoiceEventContext setInvoiceEventStat(InvoiceEventStat invoiceEventStat) {
        this.invoiceEventStat = invoiceEventStat;
        return this;
    }
}

package com.rbkmoney.magista.event;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class HandleTask implements Callable<Processor> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceChange invoiceChange;
    private final StockEvent stockEvent;
    private final Handler handler;

    public HandleTask(InvoiceChange invoiceChange, StockEvent stockEvent, Handler handler) {
        this.invoiceChange = invoiceChange;
        this.stockEvent = stockEvent;
        this.handler = handler;
    }

    @Override
    public Processor call() throws Exception {
        log.info("Start event handling, id='{}', type='{}'",
                stockEvent.getSourceEvent().getProcessingEvent().getId(), handler.getChangeType());

        return handler.handle(invoiceChange, stockEvent);
    }
}

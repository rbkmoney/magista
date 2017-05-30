package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoiceEventProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceEventStat invoiceEvent;

    public InvoiceEventProcessor(InvoiceEventService invoiceEventService, InvoiceEventStat invoiceEvent) {
        this.invoiceEventService = invoiceEventService;
        this.invoiceEvent = invoiceEvent;
    }

    @Override
    public void execute() {
        invoiceEventService.saveInvoiceEvent(invoiceEvent);
    }
}

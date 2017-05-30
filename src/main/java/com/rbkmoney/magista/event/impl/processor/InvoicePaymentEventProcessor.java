package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoicePaymentEventProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceEventStat invoicePaymentEvent;

    public InvoicePaymentEventProcessor(InvoiceEventService invoiceEventService, InvoiceEventStat invoicePaymentEvent) {
        this.invoiceEventService = invoiceEventService;
        this.invoicePaymentEvent = invoicePaymentEvent;
    }

    @Override
    public void execute() {
        invoiceEventService.saveInvoicePaymentEvent(invoicePaymentEvent);
    }
}

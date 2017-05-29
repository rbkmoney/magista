package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoiceEventStatusChangeProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceStatusChange invoiceStatusChange;

    public InvoiceEventStatusChangeProcessor(InvoiceEventService invoiceEventService, InvoiceStatusChange invoiceStatusChange) {
        this.invoiceEventService = invoiceEventService;
        this.invoiceStatusChange = invoiceStatusChange;
    }

    @Override
    public void execute() {
        invoiceEventService.changeInvoiceEventStatus(invoiceStatusChange);
    }
}

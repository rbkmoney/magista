package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.magista.service.InvoiceService;

/**
 * Created by tolkonepiu on 17/11/2016.
 */
public class InvoiceStatusChangeProcessor implements Processor {

    InvoiceService invoiceService;
    InvoiceStatusChange invoiceStatusChange;

    public InvoiceStatusChangeProcessor(InvoiceService invoiceService, InvoiceStatusChange invoiceStatusChange) {
        this.invoiceService = invoiceService;
        this.invoiceStatusChange = invoiceStatusChange;
    }

    @Override
    public void execute() {
        invoiceService.changeInvoiceStatus(invoiceStatusChange);
    }
}

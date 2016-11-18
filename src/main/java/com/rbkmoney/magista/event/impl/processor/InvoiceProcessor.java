package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.service.InvoiceService;

/**
 * Created by tolkonepiu on 17/11/2016.
 */
public class InvoiceProcessor implements Processor {

    InvoiceService invoiceService;
    Invoice invoice;

    public InvoiceProcessor(InvoiceService invoiceService, Invoice invoice) {
        this.invoiceService = invoiceService;
        this.invoice = invoice;
    }

    @Override
    public void execute() {
        invoiceService.saveInvoice(invoice);
    }
}

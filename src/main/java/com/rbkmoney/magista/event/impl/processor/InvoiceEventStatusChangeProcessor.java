package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoiceEventStatusChangeProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceEventStat invoiceStatusEvent;

    public InvoiceEventStatusChangeProcessor(InvoiceEventService invoiceEventService, InvoiceEventStat invoiceStatusEvent) {
        this.invoiceEventService = invoiceEventService;
        this.invoiceStatusEvent = invoiceStatusEvent;
    }

    @Override
    public void execute() {
        invoiceEventService.changeInvoiceEventStatus(invoiceStatusEvent);
    }
}

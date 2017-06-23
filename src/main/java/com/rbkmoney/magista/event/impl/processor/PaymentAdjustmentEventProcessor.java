package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 23/06/2017.
 */
public class PaymentAdjustmentEventProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceEventStat invoicePaymentAdjustmentEvent;

    public PaymentAdjustmentEventProcessor(InvoiceEventService invoiceEventService, InvoiceEventStat invoicePaymentAdjustmentEvent) {
        this.invoiceEventService = invoiceEventService;
        this.invoicePaymentAdjustmentEvent = invoicePaymentAdjustmentEvent;
    }

    @Override
    public void execute() {
        invoiceEventService.saveInvoicePaymentAdjustment(invoicePaymentAdjustmentEvent);
    }
}

package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 23/06/2017.
 */
public class PaymentAdjustmentEventStatusChangeProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final InvoiceEventStat invoicePaymentAdjustmentStatusEvent;

    public PaymentAdjustmentEventStatusChangeProcessor(InvoiceEventService invoiceEventService, InvoiceEventStat invoicePaymentAdjustmentStatusEvent) {
        this.invoiceEventService = invoiceEventService;
        this.invoicePaymentAdjustmentStatusEvent = invoicePaymentAdjustmentStatusEvent;
    }

    @Override
    public void execute() {
        invoiceEventService.changeInvoicePaymentAdjustmentStatus(invoicePaymentAdjustmentStatusEvent);
    }
}

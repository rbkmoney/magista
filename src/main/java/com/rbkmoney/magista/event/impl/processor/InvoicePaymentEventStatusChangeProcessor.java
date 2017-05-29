package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.magista.service.InvoiceEventService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoicePaymentEventStatusChangeProcessor implements Processor {

    private final InvoiceEventService invoiceEventService;
    private final PaymentStatusChange paymentStatusChange;

    public InvoicePaymentEventStatusChangeProcessor(InvoiceEventService invoiceEventService, PaymentStatusChange paymentStatusChange) {
        this.invoiceEventService = invoiceEventService;
        this.paymentStatusChange = paymentStatusChange;
    }

    @Override
    public void execute() {
        invoiceEventService.changeInvoicePaymentStatus(paymentStatusChange);
    }
}

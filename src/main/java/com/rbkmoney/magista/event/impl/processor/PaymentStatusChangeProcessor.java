package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.magista.service.PaymentService;

/**
 * Created by tolkonepiu on 17/11/2016.
 */
public class PaymentStatusChangeProcessor implements Processor {

    PaymentService paymentService;
    PaymentStatusChange paymentStatusChange;

    public PaymentStatusChangeProcessor(PaymentService paymentService, PaymentStatusChange paymentStatusChange) {
        this.paymentService = paymentService;
        this.paymentStatusChange = paymentStatusChange;
    }

    @Override
    public void execute() {
        paymentService.changePaymentStatus(paymentStatusChange);
    }
}

package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.service.PaymentService;

/**
 * Created by tolkonepiu on 17/11/2016.
 */
public class PaymentProcessor implements Processor {

    PaymentService paymentService;
    Payment payment;

    public PaymentProcessor(PaymentService paymentService, Payment payment) {
        this.paymentService = paymentService;
        this.payment = payment;
    }

    @Override
    public void execute() {
        paymentService.savePayment(payment);
    }
}

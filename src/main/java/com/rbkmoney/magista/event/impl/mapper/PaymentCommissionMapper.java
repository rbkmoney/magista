package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.CashFlowParty;
import com.rbkmoney.damsel.domain.InvoicePaymentCashFlow;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentEvent;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Payment;

/**
 * Created by tolkonepiu on 09/12/2016.
 */
public class PaymentCommissionMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        Payment payment = context.getPayment();

        Event event = context.getSource().getSourceEvent().getProcessingEvent();
        InvoicePaymentEvent invoicePaymentEvent = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent();
        InvoicePaymentCashFlow cashFlow = invoicePaymentEvent.getInvoicePaymentStarted().getCashFlow();
        long fee = cashFlow.getFinalCashFlow().stream()
                .filter(t -> t.getSource().getParty() == CashFlowParty.merchant
                        && t.getDestination().getParty() == CashFlowParty.system)
                .mapToLong(t -> t.getVolume().getFixed().getAmount()).sum();

        payment.setFee(fee);

        return context.setPayment(payment);
    }
}

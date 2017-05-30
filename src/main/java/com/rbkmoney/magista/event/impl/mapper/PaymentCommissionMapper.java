package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.domain.MerchantCashFlowAccount;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Payment;

import java.util.List;

/**
 * Created by tolkonepiu on 09/12/2016.
 */
public class PaymentCommissionMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        Event event = context.getSource().getSourceEvent().getProcessingEvent();
        InvoicePaymentEvent invoicePaymentEvent = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent();
        List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentEvent.getInvoicePaymentStarted().getCashFlow();

        long fee = finalCashFlowPostings.stream()
                .filter(t -> t.getSource().getAccountType().isSetMerchant()
                        && t.getSource().getAccountType().getMerchant() == MerchantCashFlowAccount.settlement
                        && t.getDestination().getAccountType().isSetSystem())
                .mapToLong(t -> t.getVolume().getAmount()).sum();

        Payment payment = context.getPayment();
        payment.setFee(fee);
        context.setPayment(payment);

        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();
        invoiceEventStat.setPaymentFee(fee);
        context.setInvoiceEventStat(invoiceEventStat);

        return context;
    }
}

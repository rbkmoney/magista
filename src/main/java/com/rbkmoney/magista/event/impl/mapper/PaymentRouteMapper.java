package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.PaymentRoute;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChangePayload;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

public class PaymentRouteMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        invoiceEventStat.setPaymentId(invoicePaymentChange.getId());
        InvoicePaymentChangePayload invoicePaymentChangePayload = invoicePaymentChange.getPayload();

        PaymentRoute paymentRoute = invoicePaymentChangePayload.getInvoicePaymentRouteChanged().getRoute();
        invoiceEventStat.setPaymentTerminalId(paymentRoute.getTerminal().getId());
        invoiceEventStat.setPaymentProviderId(paymentRoute.getProvider().getId());

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

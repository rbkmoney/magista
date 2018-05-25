package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.user_interaction.PaymentTerminalReceipt;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

public class PaymentTerminalRecieptMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat paymentEventStat = context.getInvoiceEventStat();
        paymentEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        paymentEventStat.setEventType(InvoiceEventType.PAYMENT_TERMINAL_RECIEPT);

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        paymentEventStat.setPaymentId(paymentId);

        PaymentTerminalReceipt paymentTerminalReceipt = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentSessionChange()
                .getPayload()
                .getSessionInteractionRequested()
                .getInteraction()
                .getPaymentTerminalReciept();

        paymentEventStat.setPaymentShortId(paymentTerminalReceipt.getShortPaymentId());

        return context.setInvoiceEventStat(paymentEventStat);
    }
}

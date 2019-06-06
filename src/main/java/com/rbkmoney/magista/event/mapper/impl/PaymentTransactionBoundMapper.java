package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.Mapper;
import com.rbkmoney.magista.event.mapper.PaymentMapper;

public class PaymentTransactionBoundMapper implements PaymentMapper {
    @Override
    public PaymentData map(InvoiceChange change, MachineEvent machineEvent) {
        final PaymentData paymentData = new PaymentData();
        paymentData.setEventId(machineEvent.getEventId());
        paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_TRANSACTION_BOUND);
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(machineEvent.getSourceId());

        final InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        paymentData.setPaymentId(invoicePaymentChange.getId());

        final InvoicePaymentSessionChange invoicePaymentSessionChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentSessionChange();

        final SessionTransactionBound sessionChangePayload = invoicePaymentSessionChange
                .getPayload()
                .getSessionTransactionBound();

        paymentData.setPaymentRrn(sessionChangePayload.getTrx().getAdditionalInfo().getRrn());
        paymentData.setPaymentApprovalCode(sessionChangePayload.getTrx().getAdditionalInfo().getApprovalCode());

        return paymentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_TRANSACTION_BOUND;
    }
}

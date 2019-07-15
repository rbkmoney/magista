package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.AdditionalTransactionInfo;
import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentSessionChange;
import com.rbkmoney.damsel.payment_processing.SessionTransactionBound;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import org.springframework.stereotype.Component;

@Component
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

        TransactionInfo transactionInfo = sessionChangePayload.getTrx();
        if (transactionInfo.isSetAdditionalInfo()) {
            AdditionalTransactionInfo additionalTransactionInfo = transactionInfo.getAdditionalInfo();
            paymentData.setPaymentRrn(additionalTransactionInfo.getRrn());
            paymentData.setPaymentApprovalCode(additionalTransactionInfo.getApprovalCode());
        }

        return paymentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_TRANSACTION_BOUND;
    }
}

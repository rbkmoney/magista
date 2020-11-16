package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentCaptureStarted;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentCaptureStartedMapper implements PaymentMapper {
    @Override
    public PaymentData map(InvoiceChange change, MachineEvent machineEvent) {
        final PaymentData paymentData = new PaymentData();
        paymentData.setEventId(machineEvent.getEventId());
        paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CAPTURE_STARTED);
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(machineEvent.getSourceId());

        final InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        paymentData.setPaymentId(invoicePaymentChange.getId());

        final InvoicePaymentCaptureStarted invoicePaymentCaptureStarted = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentCaptureStarted();

        if (invoicePaymentCaptureStarted.getParams().isSetCash()) {
            Cash cash = invoicePaymentCaptureStarted.getParams().getCash();
            paymentData.setPaymentAmount(cash.getAmount());
            paymentData.setPaymentCurrencyCode(cash.getCurrency().getSymbolicCode());
        }

        return paymentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CAPTURE_STARTED;
    }
}

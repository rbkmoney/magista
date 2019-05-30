package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.user_interaction.PaymentTerminalReceipt;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentTerminalRecieptEventMapper implements PaymentMapper {

    @Override
    public PaymentData map(InvoiceChange change, MachineEvent machineEvent) {

        PaymentData paymentData = new PaymentData();
        paymentData.setEventId(machineEvent.getEventId());
        paymentData.setEventType(InvoiceEventType.PAYMENT_TERMINAL_RECIEPT);
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        paymentData.setPaymentId(paymentId);

        PaymentTerminalReceipt paymentTerminalReceipt = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentSessionChange()
                .getPayload()
                .getSessionInteractionRequested()
                .getInteraction()
                .getPaymentTerminalReciept();

        paymentData.setPaymentShortId(paymentTerminalReceipt.getShortPaymentId());

        return paymentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_TERMINAL_RECIEPT;
    }
}

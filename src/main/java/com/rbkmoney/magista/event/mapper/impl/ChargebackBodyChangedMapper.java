package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackBodyChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.ChargebackMapper;

public class ChargebackBodyChangedMapper implements ChargebackMapper {

    @Override
    public ChargebackData map(InvoiceChange change, MachineEvent machineEvent) {
        ChargebackData chargebackData = new ChargebackData();
        chargebackData.setEventId(machineEvent.getEventId());
        chargebackData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        chargebackData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_BODY_CHANGED);
        chargebackData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        chargebackData.setPaymentId(paymentId);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentChargebackChange();
        chargebackData.setChargebackId(invoicePaymentChargebackChange.getId());

        InvoicePaymentChargebackBodyChanged invoicePaymentChargebackBodyChanged = invoicePaymentChargebackChange
                .getPayload()
                .getInvoicePaymentChargebackBodyChanged();
        Cash body = invoicePaymentChargebackBodyChanged.getBody();
        chargebackData.setChargebackAmount(body.getAmount());
        chargebackData.setChargebackCurrencyCode(body.getCurrency().getSymbolicCode());

        return chargebackData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CHARGEBACK_BODY_CHANGED;
    }

}

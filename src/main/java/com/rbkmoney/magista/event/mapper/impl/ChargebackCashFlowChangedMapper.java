package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackCashFlowChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.ChargebackMapper;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;

import java.util.List;
import java.util.Map;

public class ChargebackCashFlowChangedMapper implements ChargebackMapper {

    @Override
    public ChargebackData map(InvoiceChange change, MachineEvent machineEvent) {
        ChargebackData chargebackData = new ChargebackData();
        chargebackData.setEventId(machineEvent.getEventId());
        chargebackData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        chargebackData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_CASHFLOW_CHANGED);
        chargebackData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        chargebackData.setPaymentId(paymentId);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentChargebackChange();
        chargebackData.setChargebackId(invoicePaymentChargebackChange.getId());

        InvoicePaymentChargebackCashFlowChanged invoicePaymentChargebackCashFlowChanged = invoicePaymentChargebackChange
                .getPayload()
                .getInvoicePaymentChargebackCashFlowChanged();
        List<FinalCashFlowPosting> cashFlow = invoicePaymentChargebackCashFlowChanged.getCashFlow();
        Map<FeeType, Long> fees = DamselUtil.getFees(cashFlow);
        chargebackData.setChargebackFee(fees.getOrDefault(FeeType.FEE, 0L));
        chargebackData.setChargebackProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        chargebackData.setChargebackExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));

        return chargebackData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CASH_FLOW_CHANGED;
    }

}

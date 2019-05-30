package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class InvoicePaymentCashFlowChangedEventMapper implements PaymentMapper {

    @Override
    public PaymentData map(InvoiceChange change, MachineEvent machineEvent) {
        PaymentData paymentData = new PaymentData();
        paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CASH_FLOW_CHANGED);
        paymentData.setEventId(machineEvent.getEventId());
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        paymentData.setPaymentId(invoicePaymentChange.getId());

        List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentCashFlowChanged()
                .getCashFlow();

        Map<FeeType, Long> fees = DamselUtil.getFees(finalCashFlowPostings);
        paymentData.setPaymentAmount(fees.getOrDefault(FeeType.AMOUNT, 0L));
        paymentData.setPaymentFee(fees.getOrDefault(FeeType.FEE, 0L));
        paymentData.setPaymentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));
        paymentData.setPaymentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));

        return paymentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CASH_FLOW_CHANGED;
    }
}

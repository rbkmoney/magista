package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.RefundMapper;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RefundCreatedMapper implements RefundMapper {

    @Override
    public RefundData map(InvoiceChange change, MachineEvent machineEvent) {
        RefundData refund = new RefundData();

        refund.setEventId(machineEvent.getEventId());
        refund.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        refund.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_CREATED);
        refund.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        refund.setPaymentId(paymentId);

        InvoicePaymentRefundCreated invoicePaymentRefundCreated = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange()
                .getPayload()
                .getInvoicePaymentRefundCreated();

        InvoicePaymentRefund invoicePaymentRefund = invoicePaymentRefundCreated
                .getRefund();

        refund.setRefundId(invoicePaymentRefund.getId());
        refund.setRefundStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentRefund.getStatus(), RefundStatus.class)
        );
        refund.setRefundReason(invoicePaymentRefund.getReason());
        refund.setRefundCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePaymentRefund.getCreatedAt())
        );
        if (invoicePaymentRefund.isSetCash()) {
            Cash refundCash = invoicePaymentRefund.getCash();
            refund.setRefundAmount(refundCash.getAmount());
            refund.setRefundCurrencyCode(refundCash.getCurrency().getSymbolicCode());
        }
        refund.setRefundDomainRevision(invoicePaymentRefund.getDomainRevision());

        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentRefundCreated.getCashFlow());
        refund.setRefundFee(fees.getOrDefault(FeeType.FEE, 0L));
        refund.setRefundProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        refund.setRefundExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));

        return refund;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_REFUND_CREATED;
    }

}

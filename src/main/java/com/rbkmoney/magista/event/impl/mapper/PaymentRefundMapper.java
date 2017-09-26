package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.PostingType;

import java.util.Map;

public class PaymentRefundMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat paymentRefundEventStat = context.getInvoiceEventStat();
        paymentRefundEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        paymentRefundEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_CREATED);

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        paymentRefundEventStat.setPaymentId(paymentId);

        InvoicePaymentRefundCreated invoicePaymentRefundCreated = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange()
                .getPayload()
                .getInvoicePaymentRefundCreated();

        InvoicePaymentRefund refund = invoicePaymentRefundCreated
                .getRefund();

        paymentRefundEventStat.setPaymentRefundId(refund.getId());
        paymentRefundEventStat.setPaymentRefundStatus(
                TBaseUtil.unionFieldToEnum(refund.getStatus(), InvoicePaymentRefundStatus.class)
        );
        paymentRefundEventStat.setPaymentRefundReason(refund.getReason());
        paymentRefundEventStat.setEventCreatedAt(
                TypeUtil.stringToLocalDateTime(refund.getCreatedAt())
        );

        Map<PostingType, Long> commissions = DamselUtil.calculateCommissions(invoicePaymentRefundCreated.getCashFlow());

        paymentRefundEventStat.setPaymentRefundFee(commissions.get(PostingType.FEE));
        paymentRefundEventStat.setPaymentRefundProviderFee(commissions.get(PostingType.PROVIDER_FEE));
        paymentRefundEventStat.setPaymentRefundExternalFee(commissions.get(PostingType.EXTERNAL_FEE));

        return context.setInvoiceEventStat(paymentRefundEventStat);
    }
}

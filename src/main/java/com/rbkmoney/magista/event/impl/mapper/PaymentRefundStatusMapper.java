package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

public class PaymentRefundStatusMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat paymentRefundEventStat = context.getInvoiceEventStat();
        paymentRefundEventStat.setEventCategory(InvoiceEventCategory.REFUND);
        paymentRefundEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_STATUS_CHANGED);

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        paymentRefundEventStat.setPaymentId(paymentId);

        InvoicePaymentRefundChange refundChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange();

        paymentRefundEventStat.setPaymentRefundId(refundChange.getId());

        paymentRefundEventStat.setPaymentRefundStatus(
                TBaseUtil.unionFieldToEnum(
                        refundChange.getPayload().getInvoicePaymentRefundStatusChanged().getStatus(),
                        InvoicePaymentRefundStatus.class
                )
        );
        return context.setInvoiceEventStat(paymentRefundEventStat);
    }
}

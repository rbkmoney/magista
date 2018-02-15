package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentStatusChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;

/**
 * Created by tolkonepiu on 23/06/2017.
 */
public class PaymentAdjustmentStatusMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoicePaymentAdjustmentStatusEvent = context.getInvoiceEventStat();
        invoicePaymentAdjustmentStatusEvent.setEventCategory(InvoiceEventCategory.ADJUSTMENT);
        invoicePaymentAdjustmentStatusEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED);

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        invoicePaymentAdjustmentStatusEvent.setPaymentId(paymentId);

        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange.getPayload()
                .getInvoicePaymentAdjustmentChange();
        String adjustmentId = adjustmentChange.getId();
        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentId(adjustmentId);

        InvoicePaymentAdjustmentStatusChanged adjustmentStatusChanged = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentStatusChanged();

        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentStatus(
                TBaseUtil.unionFieldToEnum(adjustmentStatusChanged.getStatus(), AdjustmentStatus.class)
        );

        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(adjustmentStatusChanged.getStatus())
        );

        return context.setInvoiceEventStat(invoicePaymentAdjustmentStatusEvent);
    }
}

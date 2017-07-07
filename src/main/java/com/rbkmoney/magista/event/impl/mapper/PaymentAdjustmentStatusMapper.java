package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
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

        InvoicePaymentAdjustmentStatusChanged adjustmentStatusChanged = context.getSource()
                .getSourceEvent()
                .getProcessingEvent()
                .getPayload()
                .getInvoiceEvent()
                .getInvoicePaymentEvent()
                .getInvoicePaymentAdjustmentEvent()
                .getInvoicePaymentAdjustmentStatusChanged();

        String paymentId = adjustmentStatusChanged.getPaymentId();
        invoicePaymentAdjustmentStatusEvent.setPaymentId(paymentId);

        String adjustmentId = adjustmentStatusChanged.getAdjustmentId();
        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentId(adjustmentId);

        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentStatus(
                TBaseUtil.unionFieldToEnum(adjustmentStatusChanged.getStatus(), AdjustmentStatus.class)
        );

        invoicePaymentAdjustmentStatusEvent.setPaymentAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(adjustmentStatusChanged.getStatus())
        );

        return context.setInvoiceEventStat(invoicePaymentAdjustmentStatusEvent);
    }
}

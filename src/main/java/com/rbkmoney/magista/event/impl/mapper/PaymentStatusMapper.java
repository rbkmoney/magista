package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

/**
 * Created by tolkonepiu on 14/11/2016.
 */
public class PaymentStatusMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();
        invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED);

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        invoiceEventStat.setPaymentId(invoicePaymentChange.getId());

        InvoicePaymentStatusChanged invoicePaymentStatusChanged = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentStatusChanged();

        invoiceEventStat.setPaymentStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentStatusChanged.getStatus(), InvoicePaymentStatus.class)
        );

        if (invoicePaymentStatusChanged.getStatus().isSetFailed()) {
            OperationFailure operationFailure = invoicePaymentStatusChanged.getStatus().getFailed().getFailure();
            invoiceEventStat.setPaymentOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                invoiceEventStat.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                invoiceEventStat.setPaymentExternalFailureReason(failure.getReason());
            }
        }

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

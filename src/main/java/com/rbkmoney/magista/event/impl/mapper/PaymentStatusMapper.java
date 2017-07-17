package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.ExternalFailure;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
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
            //TODO operation timeout?
            if (operationFailure.isSetExternalFailure()) {
                ExternalFailure externalFailure = operationFailure.getExternalFailure();
                invoiceEventStat.setPaymentStatusFailureCode(externalFailure.getCode());
                invoiceEventStat.setPaymentStatusFailureDescription(externalFailure.getDescription());
            }
        }

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

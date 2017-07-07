package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.OperationFailure;
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

        InvoicePaymentStatusChanged invoicePaymentStatusChanged = context
                .getSource()
                .getSourceEvent()
                .getProcessingEvent()
                .getPayload()
                .getInvoiceEvent()
                .getInvoicePaymentEvent()
                .getInvoicePaymentStatusChanged();

        invoiceEventStat.setPaymentId(invoicePaymentStatusChanged.getPaymentId());
        invoiceEventStat.setPaymentStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentStatusChanged.getStatus(), InvoicePaymentStatus.class)
        );

        if (invoicePaymentStatusChanged.getStatus().isSetFailed()) {
            OperationFailure operationFailure = invoicePaymentStatusChanged.getStatus().getFailed().getFailure();
            invoiceEventStat.setPaymentStatusFailureCode(operationFailure.getCode());
            invoiceEventStat.setPaymentStatusFailureDescription(operationFailure.getDescription());
        }

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

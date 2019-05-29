package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.RefundHandler;
import org.springframework.stereotype.Component;

@Component
public class RefundStatusChangedHandler implements RefundHandler {

    @Override
    public RefundData handle(InvoiceChange change, MachineEvent machineEvent) {
        RefundData refundData = new RefundData();

        refundData.setEventId(machineEvent.getEventId());
        refundData.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_STATUS_CHANGED);
        refundData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));

        String invoiceId = machineEvent.getSourceId();
        refundData.setInvoiceId(invoiceId);

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        refundData.setPaymentId(paymentId);

        InvoicePaymentRefundChange refundChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange();

        String refundId = refundChange.getId();
        refundData.setRefundId(refundId);

        InvoicePaymentRefundStatus status = refundChange.getPayload()
                .getInvoicePaymentRefundStatusChanged()
                .getStatus();

        refundData.setRefundStatus(TBaseUtil.unionFieldToEnum(
                status,
                RefundStatus.class
        ));
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            refundData.setRefundOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                refundData.setRefundExternalFailure(TErrorUtil.toStringVal(failure));
                refundData.setRefundExternalFailureReason(failure.getReason());
            }
        }

        return refundData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_REFUND_STATUS_CHANGED;
    }

}

package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
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
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefundStatusChangedHandler implements Handler<InvoiceChange, MachineEvent> {

    private final PaymentRefundService paymentRefundService;

    @Autowired
    public RefundStatusChangedHandler(PaymentRefundService paymentRefundService) {
        this.paymentRefundService = paymentRefundService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {
        Refund refund = new Refund();

        refund.setEventId(machineEvent.getEventId());
        refund.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_STATUS_CHANGED);
        refund.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));

        String invoiceId = machineEvent.getSourceId();
        refund.setInvoiceId(invoiceId);

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        refund.setPaymentId(paymentId);

        InvoicePaymentRefundChange refundChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange();

        String refundId = refundChange.getId();
        refund.setRefundId(refundId);

        InvoicePaymentRefundStatus status = refundChange.getPayload().getInvoicePaymentRefundStatusChanged().getStatus();
        refund.setRefundStatus(TBaseUtil.unionFieldToEnum(
                status,
                RefundStatus.class
        ));
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            refund.setRefundOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                refund.setRefundExternalFailure(TErrorUtil.toStringVal(failure));
                refund.setRefundExternalFailureReason(failure.getReason());
            }
        }

        return () -> paymentRefundService.savePaymentRefund(refund);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_REFUND_STATUS_CHANGED;
    }

}

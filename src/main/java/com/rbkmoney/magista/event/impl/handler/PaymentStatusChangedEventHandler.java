package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.domain.InvoicePaymentCaptured;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusChangedEventHandler implements Handler<InvoiceChange, MachineEvent> {

    private final PaymentService paymentService;

    @Autowired
    public PaymentStatusChangedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {

        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED);
        paymentEvent.setEventId(machineEvent.getEventId());

        paymentEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentEvent.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        paymentEvent.setPaymentId(invoicePaymentChange.getId());

        InvoicePaymentStatusChanged invoicePaymentStatusChanged = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentStatusChanged();

        paymentEvent.setPaymentStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentStatusChanged.getStatus(), InvoicePaymentStatus.class)
        );

        if (invoicePaymentStatusChanged.getStatus().isSetCaptured()) {
            InvoicePaymentCaptured invoicePaymentCaptured = invoicePaymentStatusChanged.getStatus().getCaptured();
            if (invoicePaymentCaptured.isSetCost()) {
                Cash cost = invoicePaymentCaptured.getCost();
                paymentEvent.setPaymentAmount(cost.getAmount());
                paymentEvent.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());
            }
        }

        if (invoicePaymentStatusChanged.getStatus().isSetFailed()) {
            OperationFailure operationFailure = invoicePaymentStatusChanged.getStatus().getFailed().getFailure();
            paymentEvent.setPaymentOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                paymentEvent.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                paymentEvent.setPaymentExternalFailureReason(failure.getReason());
            }
        }

        return () -> paymentService.savePaymentChange(paymentEvent);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_STATUS_CHANGED;
    }
}

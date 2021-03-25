package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.AdjustmentMapper;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdjustmentCreatedMapper implements AdjustmentMapper {

    @Override
    public AdjustmentData map(InvoiceChange change, MachineEvent machineEvent) {
        AdjustmentData adjustmentData = new AdjustmentData();

        adjustmentData.setEventId(machineEvent.getEventId());
        adjustmentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        adjustmentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);
        adjustmentData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        adjustmentData.setPaymentId(paymentId);

        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentAdjustmentChange();
        adjustmentData.setAdjustmentId(adjustmentChange.getId());

        InvoicePaymentAdjustment invoicePaymentAdjustment = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentCreated()
                .getAdjustment();

        adjustmentData.setAdjustmentCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getCreatedAt())
        );

        adjustmentData.setAdjustmentReason(invoicePaymentAdjustment.getReason());

        adjustmentData.setAdjustmentStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentAdjustment.getStatus(), AdjustmentStatus.class));
        adjustmentData.setAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(invoicePaymentAdjustment.getStatus())
        );
        adjustmentData.setAdjustmentDomainRevision(invoicePaymentAdjustment.getDomainRevision());

        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentAdjustment.getNewCashFlow());
        adjustmentData.setAdjustmentFee(fees.getOrDefault(FeeType.FEE, 0L));
        adjustmentData.setAdjustmentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        adjustmentData.setAdjustmentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));

        if (invoicePaymentAdjustment.isSetState()) {
            InvoicePaymentAdjustmentState paymentAdjustmentState = invoicePaymentAdjustment.getState();
            if (invoicePaymentAdjustment.getState().isSetCashFlow()) {
                adjustmentData.setAdjustmentDomainRevision(
                        paymentAdjustmentState.getCashFlow().getScenario().getDomainRevision());
            }
            if (invoicePaymentAdjustment.getState().isSetStatusChange()) {
                InvoicePaymentAdjustmentStatusChangeState paymentAdjustmentStatusChangeState =
                        paymentAdjustmentState.getStatusChange();
                InvoicePaymentAdjustmentStatusChange paymentAdjustmentStatusChange =
                        paymentAdjustmentStatusChangeState.getScenario();

                InvoicePaymentStatus invoicePaymentStatus =
                        TBaseUtil.unionFieldToEnum(
                                paymentAdjustmentStatusChange.getTargetStatus(),
                                InvoicePaymentStatus.class
                        );
                adjustmentData.setPaymentStatus(invoicePaymentStatus);

                if (paymentAdjustmentStatusChange.getTargetStatus().isSetFailed()) {
                    OperationFailure operationFailure =
                            paymentAdjustmentStatusChange.getTargetStatus().getFailed().getFailure();
                    adjustmentData.setPaymentOperationFailureClass(
                            TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
                    );
                    if (operationFailure.isSetFailure()) {
                        Failure failure = operationFailure.getFailure();
                        adjustmentData.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                        adjustmentData.setPaymentExternalFailureReason(failure.getReason());
                    }
                }
            }
        }

        return adjustmentData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_CREATED;
    }

}

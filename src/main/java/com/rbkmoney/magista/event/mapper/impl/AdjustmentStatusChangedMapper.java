package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentStatusChanged;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.AdjustmentMapper;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentStatusChangedMapper implements AdjustmentMapper {

    @Override
    public AdjustmentData map(InvoiceChange change, MachineEvent machineEvent) {
        AdjustmentData adjustment = new AdjustmentData();

        adjustment.setEventId(machineEvent.getEventId());
        adjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        adjustment.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED);
        adjustment.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        adjustment.setPaymentId(paymentId);

        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentAdjustmentChange();
        adjustment.setAdjustmentId(adjustmentChange.getId());

        InvoicePaymentAdjustmentStatusChanged adjustmentStatusChanged = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentStatusChanged();

        InvoicePaymentAdjustmentStatus invoicePaymentAdjustmentStatus = adjustmentStatusChanged.getStatus();
        adjustment.setAdjustmentStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentAdjustmentStatus, AdjustmentStatus.class)
        );
        adjustment.setAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(invoicePaymentAdjustmentStatus)
        );

        return adjustment;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED;
    }

}

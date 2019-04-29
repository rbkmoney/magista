package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentAdjustmentService;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdjustmentCreatedHandler implements Handler<InvoiceChange, MachineEvent> {

    private final PaymentAdjustmentService paymentAdjustmentService;

    @Autowired
    public AdjustmentCreatedHandler(PaymentAdjustmentService paymentAdjustmentService) {
        this.paymentAdjustmentService = paymentAdjustmentService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {
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

        adjustmentData.setAdjustmentStatus(TBaseUtil.unionFieldToEnum(invoicePaymentAdjustment.getStatus(), AdjustmentStatus.class));
        adjustmentData.setAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(invoicePaymentAdjustment.getStatus())
        );
        adjustmentData.setAdjustmentDomainRevision(invoicePaymentAdjustment.getDomainRevision());

        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentAdjustment.getNewCashFlow());
        adjustmentData.setAdjustmentFee(fees.getOrDefault(FeeType.FEE, 0L));
        adjustmentData.setAdjustmentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        adjustmentData.setAdjustmentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));

        return () -> paymentAdjustmentService.savePaymentAdjustment(adjustmentData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_CREATED;
    }

}

package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentAdjustmentService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tolkonepiu on 21/06/2017.
 */
@Component
public class AdjustmentStatusChangedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentAdjustmentService paymentAdjustmentService;

    @Autowired
    public AdjustmentStatusChangedHandler(PaymentAdjustmentService paymentAdjustmentService) {
        this.paymentAdjustmentService = paymentAdjustmentService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Adjustment adjustment = new Adjustment();

        Event event = parent.getSourceEvent().getProcessingEvent();
        adjustment.setEventId(event.getId());
        adjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        adjustment.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED);
        adjustment.setInvoiceId(event.getSource().getInvoiceId());

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

        return () -> paymentAdjustmentService.savePaymentAdjustment(adjustment);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED;
    }

}

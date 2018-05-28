package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
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
import com.rbkmoney.magista.util.FeeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by tolkonepiu on 21/06/2017.
 */
@Component
public class AdjustmentCreatedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentAdjustmentService paymentAdjustmentService;

    @Autowired
    public AdjustmentCreatedHandler(PaymentAdjustmentService paymentAdjustmentService) {
        this.paymentAdjustmentService = paymentAdjustmentService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Adjustment adjustment = new Adjustment();

        Event event = parent.getSourceEvent().getProcessingEvent();
        adjustment.setEventId(event.getId());
        adjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        adjustment.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);
        adjustment.setInvoiceId(event.getSource().getInvoiceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        adjustment.setPaymentId(paymentId);

        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentAdjustmentChange();
        adjustment.setAdjustmentId(adjustmentChange.getId());

        InvoicePaymentAdjustment invoicePaymentAdjustment = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentCreated()
                .getAdjustment();

        adjustment.setAdjustmentCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getCreatedAt())
        );

        adjustment.setAdjustmentReason(invoicePaymentAdjustment.getReason());

        adjustment.setAdjustmentStatus(TBaseUtil.unionFieldToEnum(invoicePaymentAdjustment.getStatus(), AdjustmentStatus.class));
        adjustment.setAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(invoicePaymentAdjustment.getStatus())
        );
        adjustment.setAdjustmentDomainRevision(invoicePaymentAdjustment.getDomainRevision());

        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentAdjustment.getNewCashFlow());
        adjustment.setAdjustmentFee(fees.get(FeeType.FEE));
        adjustment.setAdjustmentProviderFee(fees.get(FeeType.PROVIDER_FEE));
        adjustment.setAdjustmentExternalFee(fees.get(FeeType.EXTERNAL_FEE));

        return () -> paymentAdjustmentService.savePaymentAdjustment(adjustment);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_CREATED;
    }

}

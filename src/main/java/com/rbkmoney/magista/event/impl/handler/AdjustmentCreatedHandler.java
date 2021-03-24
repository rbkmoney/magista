package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentState;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentStatusChange;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentStatusChangeState;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
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
        Event event = parent.getSourceEvent().getProcessingEvent();
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentAdjustmentChange();
        InvoicePaymentAdjustment invoicePaymentAdjustment = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentCreated()
                .getAdjustment();
        String paymentId = invoicePaymentChange.getId();
        Long oldAmount = DamselUtil.computeMerchantAmount(invoicePaymentAdjustment.getOldCashFlowInverse());
        Long newAmount = DamselUtil.computeMerchantAmount(invoicePaymentAdjustment.getNewCashFlow());
        Long amount = oldAmount + newAmount;
        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentAdjustment.getNewCashFlow());

        Adjustment adjustment = new Adjustment();
        adjustment.setEventId((long) event.getSequence());
        adjustment.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        adjustment.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);
        adjustment.setInvoiceId(event.getSource().getInvoiceId());
        adjustment.setPaymentId(paymentId);
        adjustment.setAdjustmentId(adjustmentChange.getId());
        adjustment.setAdjustmentCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePaymentAdjustment.getCreatedAt())
        );
        adjustment.setAdjustmentReason(invoicePaymentAdjustment.getReason());
        adjustment.setAdjustmentStatus(TBaseUtil.unionFieldToEnum(invoicePaymentAdjustment.getStatus(), AdjustmentStatus.class));
        adjustment.setAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(invoicePaymentAdjustment.getStatus())
        );
        adjustment.setAdjustmentDomainRevision(invoicePaymentAdjustment.getDomainRevision());
        adjustment.setAdjustmentFee(fees.getOrDefault(FeeType.FEE, 0L));
        adjustment.setAdjustmentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        adjustment.setAdjustmentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));
        if (invoicePaymentAdjustment.isSetState()) {
            InvoicePaymentAdjustmentState paymentAdjustmentState = invoicePaymentAdjustment.getState();
            if (invoicePaymentAdjustment.getState().isSetCashFlow()) {
                adjustment.setAdjustmentDomainRevision(
                        paymentAdjustmentState.getCashFlow().getScenario().getDomainRevision());
            }
            if (invoicePaymentAdjustment.getState().isSetStatusChange()) {
                InvoicePaymentAdjustmentStatusChangeState paymentAdjustmentStatusChangeState =
                        paymentAdjustmentState.getStatusChange();
                InvoicePaymentAdjustmentStatusChange paymentAdjustmentStatusChange =
                        paymentAdjustmentStatusChangeState.getScenario();
                InvoicePaymentStatus invoicePaymentStatus = TBaseUtil.unionFieldToEnum(
                        paymentAdjustmentStatusChange.getTargetStatus(),
                        InvoicePaymentStatus.class);

                adjustment.setPaymentStatus(invoicePaymentStatus);

                if (paymentAdjustmentStatusChange.getTargetStatus().isSetFailed()) {
                    OperationFailure operationFailure =
                            paymentAdjustmentStatusChange.getTargetStatus().getFailed().getFailure();
                    adjustment.setPaymentOperationFailureClass(
                            TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
                    );
                    if (operationFailure.isSetFailure()) {
                        Failure failure = operationFailure.getFailure();
                        adjustment.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                        adjustment.setPaymentExternalFailureReason(failure.getReason());
                    }
                }
            }
        }

        adjustment.setAdjustmentAmount(amount);

        return () -> paymentAdjustmentService.savePaymentAdjustment(adjustment);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_ADJUSTMENT_CREATED;
    }

}

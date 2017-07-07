package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.CashFlowAccount;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;

import java.util.Map;

/**
 * Created by tolkonepiu on 22/06/2017.
 */
public class PaymentAdjustmentMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentAdjustmentCreated adjustmentCreated = context.getSource()
                .getSourceEvent()
                .getProcessingEvent()
                .getPayload()
                .getInvoiceEvent()
                .getInvoicePaymentEvent()
                .getInvoicePaymentAdjustmentEvent()
                .getInvoicePaymentAdjustmentCreated();


        String paymentId = adjustmentCreated.getPaymentId();
        invoiceEventStat.setPaymentId(paymentId);

        InvoicePaymentAdjustment adjustment = adjustmentCreated.getAdjustment();
        invoiceEventStat.setPaymentAdjustmentId(adjustment.getId());
        invoiceEventStat.setPaymentAdjustmentReason(adjustment.getReason());

        invoiceEventStat.setPaymentAdjustmentStatus(TBaseUtil.unionFieldToEnum(adjustment.getStatus(), AdjustmentStatus.class));
        invoiceEventStat.setPaymentAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(adjustment.getStatus())
        );

        Map<CashFlowAccount._Fields, Long> commissions = DamselUtil.calculateCommissions(adjustment.getNewCashFlow());

        invoiceEventStat.setPaymentAmount(commissions.get(CashFlowAccount._Fields.MERCHANT));
        invoiceEventStat.setPaymentAdjustmentFee(commissions.get(CashFlowAccount._Fields.SYSTEM));
        invoiceEventStat.setPaymentAdjustmentProviderFee(commissions.get(CashFlowAccount._Fields.PROVIDER));
        invoiceEventStat.setPaymentAdjustmentExternalFee(commissions.get(CashFlowAccount._Fields.EXTERNAL));

        invoiceEventStat.setPaymentAdjustmentCreatedAt(
                TypeUtil.stringToLocalDateTime(adjustment.getCreatedAt())
        );

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

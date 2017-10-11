package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoicePaymentAdjustment;
import com.rbkmoney.damsel.domain.MerchantCashFlowAccount;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentAdjustmentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;

import java.util.Map;

/**
 * Created by tolkonepiu on 22/06/2017.
 */
public class PaymentAdjustmentMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        invoiceEventStat.setPaymentId(paymentId);

        InvoicePaymentAdjustmentChange adjustmentChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentAdjustmentChange();
        invoiceEventStat.setPaymentAdjustmentId(adjustmentChange.getId());

        InvoicePaymentAdjustment adjustment = adjustmentChange
                .getPayload()
                .getInvoicePaymentAdjustmentCreated()
                .getAdjustment();

        invoiceEventStat.setPaymentAdjustmentReason(adjustment.getReason());

        invoiceEventStat.setPaymentAdjustmentStatus(TBaseUtil.unionFieldToEnum(adjustment.getStatus(), AdjustmentStatus.class));
        invoiceEventStat.setPaymentAdjustmentStatusCreatedAt(
                DamselUtil.getAdjustmentStatusCreatedAt(adjustment.getStatus())
        );

        invoiceEventStat.setPaymentAmount(
                DamselUtil.getAmount(adjustment.getNewCashFlow(),
                        posting -> posting.getSource().getAccountType().isSetProvider()
                                && posting.getDestination().getAccountType().isSetMerchant()
                                && posting.getDestination().getAccountType().getMerchant() == MerchantCashFlowAccount.settlement)
        );

        Map<FeeType, Long> fees = DamselUtil.getFees(adjustment.getNewCashFlow());

        invoiceEventStat.setPaymentAdjustmentFee(fees.get(FeeType.FEE));
        invoiceEventStat.setPaymentAdjustmentProviderFee(fees.get(FeeType.PROVIDER_FEE));
        invoiceEventStat.setPaymentAdjustmentExternalFee(fees.get(FeeType.EXTERNAL_FEE));

        invoiceEventStat.setPaymentAdjustmentCreatedAt(
                TypeUtil.stringToLocalDateTime(adjustment.getCreatedAt())
        );

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}

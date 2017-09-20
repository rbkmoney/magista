package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.PostingType;

import java.util.List;
import java.util.Map;

/**
 * Created by tolkonepiu on 09/12/2016.
 */
public class PaymentCommissionMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentStarted invoicePaymentStarted = context
                .getInvoiceChange()
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentStarted.getCashFlow();

        Map<PostingType, Long> commissions = DamselUtil.calculateCommissions(finalCashFlowPostings);

        invoiceEventStat.setPaymentFee(commissions.get(PostingType.FEE));
        invoiceEventStat.setPaymentExternalFee(commissions.get(PostingType.EXTERNAL_FEE));
        invoiceEventStat.setPaymentProviderFee(commissions.get(PostingType.PROVIDER_FEE));

        return context.setInvoiceEventStat(invoiceEventStat);
    }

}

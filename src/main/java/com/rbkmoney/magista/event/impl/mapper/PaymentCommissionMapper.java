package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChangePayload;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by tolkonepiu on 09/12/2016.
 */
public class PaymentCommissionMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentChange invoicePaymentChange = context
                .getInvoiceChange()
                .getInvoicePaymentChange();

        invoiceEventStat.setPaymentId(invoicePaymentChange.getId());
        InvoicePaymentChangePayload invoicePaymentChangePayload = invoicePaymentChange.getPayload();
        List<FinalCashFlowPosting> finalCashFlowPostings = Collections.emptyList();
        if (invoicePaymentChangePayload.isSetInvoicePaymentStarted()
                && invoicePaymentChangePayload.getInvoicePaymentStarted().isSetCashFlow()) {
            finalCashFlowPostings = invoicePaymentChangePayload.getInvoicePaymentStarted().getCashFlow();
        } else if (invoicePaymentChangePayload.isSetInvoicePaymentCashFlowChanged()) {
            finalCashFlowPostings = invoicePaymentChangePayload.getInvoicePaymentCashFlowChanged().getCashFlow();
        }

        Map<FeeType, Long> fees = DamselUtil.getFees(finalCashFlowPostings);
        invoiceEventStat.setPaymentFee(fees.get(FeeType.FEE));
        invoiceEventStat.setPaymentExternalFee(fees.get(FeeType.EXTERNAL_FEE));
        invoiceEventStat.setPaymentProviderFee(fees.get(FeeType.PROVIDER_FEE));

        return context.setInvoiceEventStat(invoiceEventStat);
    }

}

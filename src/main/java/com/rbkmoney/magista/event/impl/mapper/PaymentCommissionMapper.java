package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.CashFlowAccount;
import com.rbkmoney.damsel.domain.ExternalCashFlowAccount;
import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.domain.MerchantCashFlowAccount;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by tolkonepiu on 09/12/2016.
 */
public class PaymentCommissionMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        Event event = context.getSource().getSourceEvent().getProcessingEvent();
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        InvoicePaymentEvent invoicePaymentEvent = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent();
        List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentEvent.getInvoicePaymentStarted().getCashFlow();

        Map<CashFlowAccount._Fields, Long> commissions = DamselUtil.calculateCommissions(finalCashFlowPostings);

        invoiceEventStat.setPaymentFee(commissions.get(CashFlowAccount._Fields.SYSTEM));
        invoiceEventStat.setPaymentExternalFee(commissions.get(CashFlowAccount._Fields.EXTERNAL));
        invoiceEventStat.setPaymentProviderFee(commissions.get(CashFlowAccount._Fields.PROVIDER));

        return context.setInvoiceEventStat(invoiceEventStat);
    }

}

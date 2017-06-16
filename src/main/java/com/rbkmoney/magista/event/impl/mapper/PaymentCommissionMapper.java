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

        Map<CashFlowAccount._Fields, Long> commissions = finalCashFlowPostings.stream()
                .collect(
                        Collectors.groupingBy(
                                PaymentCommissionMapper::getCommissionType,
                                Collectors.summingLong(posting -> posting.getVolume().getAmount())
                        )
                );

        invoiceEventStat.setPaymentFee(commissions.get(CashFlowAccount._Fields.SYSTEM));
        invoiceEventStat.setPaymentExternalFee(commissions.get(CashFlowAccount._Fields.EXTERNAL));
        invoiceEventStat.setPaymentProviderFee(commissions.get(CashFlowAccount._Fields.PROVIDER));

        context.setInvoiceEventStat(invoiceEventStat);

        return context;
    }

    public static CashFlowAccount._Fields getCommissionType(FinalCashFlowPosting cashFlowPosting) {
        CashFlowAccount source = cashFlowPosting.getSource().getAccountType();
        CashFlowAccount destination = cashFlowPosting.getDestination().getAccountType();

        if (source.isSetProvider()
                && destination.isSetMerchant()
                && destination.getMerchant() == MerchantCashFlowAccount.settlement) {
            return CashFlowAccount._Fields.MERCHANT;
        }

        if (source.isSetMerchant()
                && source.getMerchant() == MerchantCashFlowAccount.settlement
                && destination.isSetSystem()) {
            return CashFlowAccount._Fields.SYSTEM;
        }

        if (source.isSetSystem()
                && destination.isSetExternal()
                && destination.getExternal() == ExternalCashFlowAccount.outcome) {
            return CashFlowAccount._Fields.EXTERNAL;
        }

        if (source.isSetSystem()
                && destination.isSetProvider()) {
            return CashFlowAccount._Fields.PROVIDER;
        }

        throw new IllegalArgumentException(String.format("Unknown posting path, source - '%s', destination - '%s'", source.getSetField(), destination.getSetField()));
    }
}

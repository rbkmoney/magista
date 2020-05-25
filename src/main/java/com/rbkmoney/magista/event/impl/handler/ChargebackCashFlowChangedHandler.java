package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.FinalCashFlowPosting;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentChargebackService;
import com.rbkmoney.magista.util.DamselUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargebackCashFlowChangedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentChargebackService paymentChargebackService;

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        ChargebackData chargebackData = new ChargebackData();
        chargebackData.setEventId(event.getId());
        chargebackData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        chargebackData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_CASHFLOW_CHANGED);
        chargebackData.setInvoiceId(event.getSource().getInvoiceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        chargebackData.setPaymentId(paymentId);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentChargebackChange();
        chargebackData.setChargebackId(invoicePaymentChargebackChange.getId());

        InvoicePaymentChargebackCashFlowChanged invoicePaymentChargebackCashFlowChanged = invoicePaymentChargebackChange
                .getPayload()
                .getInvoicePaymentChargebackCashFlowChanged();
        List<FinalCashFlowPosting> cashFlow = invoicePaymentChargebackCashFlowChanged.getCashFlow();
        chargebackData.setChargebackAmount(Math.negateExact(DamselUtil.computeMerchantAmount(cashFlow)));

        return () -> paymentChargebackService.savePaymentChargeback(chargebackData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CHARGEBACK_CASH_FLOW_CHANGED;
    }
}

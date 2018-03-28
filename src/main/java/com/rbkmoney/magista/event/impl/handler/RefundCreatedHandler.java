package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentRefundCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentRefundService;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RefundCreatedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentRefundService paymentRefundService;

    @Autowired
    public RefundCreatedHandler(PaymentRefundService paymentRefundService) {
        this.paymentRefundService = paymentRefundService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Refund refund = new Refund();

        Event event = parent.getSourceEvent().getProcessingEvent();
        refund.setEventId(event.getId());
        refund.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        refund.setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_CREATED);
        refund.setInvoiceId(event.getSource().getInvoiceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        refund.setPaymentId(paymentId);

        InvoicePaymentRefundCreated invoicePaymentRefundCreated = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentRefundChange()
                .getPayload()
                .getInvoicePaymentRefundCreated();

        InvoicePaymentRefund invoicePaymentRefund = invoicePaymentRefundCreated
                .getRefund();

        refund.setRefundId(invoicePaymentRefund.getId());
        refund.setRefundStatus(
                TBaseUtil.unionFieldToEnum(invoicePaymentRefund.getStatus(), RefundStatus.class)
        );
        refund.setRefundReason(invoicePaymentRefund.getReason());
        refund.setRefundCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePaymentRefund.getCreatedAt())
        );
        if (invoicePaymentRefund.isSetCash()) {
            Cash refundCash = invoicePaymentRefund.getCash();
            refund.setRefundAmount(refundCash.getAmount());
            refund.setRefundCurrencyCode(refundCash.getCurrency().getSymbolicCode());
        }

        Map<FeeType, Long> fees = DamselUtil.getFees(invoicePaymentRefundCreated.getCashFlow());
        refund.setRefundFee(fees.get(FeeType.FEE));
        refund.setRefundProviderFee(fees.get(FeeType.PROVIDER_FEE));
        refund.setRefundExternalFee(fees.get(FeeType.EXTERNAL_FEE));

        return () -> paymentRefundService.savePaymentRefund(refund);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_REFUND_CREATED;
    }

}

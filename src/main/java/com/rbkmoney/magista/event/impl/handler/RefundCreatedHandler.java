package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentRefundMapper;
import com.rbkmoney.magista.service.InvoiceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RefundCreatedHandler extends AbstractInvoiceEventHandler {

    private final InvoiceEventService invoiceEventService;

    @Autowired
    public RefundCreatedHandler(InvoiceEventService invoiceEventService) {
        this.invoiceEventService = invoiceEventService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        InvoiceEventContext context = generateContext(change, parent);
        return () -> invoiceEventService.saveInvoicePaymentRefund(context.getInvoiceEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_REFUND_CREATED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new InvoiceMapper(),
                new PaymentRefundMapper()
        );
    }
}

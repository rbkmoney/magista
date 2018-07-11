package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentCommissionMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentStatusMapper;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.service.InvoiceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InvoicePaymentCashFlowChangedHandler extends AbstractInvoiceEventHandler {

    private final InvoiceEventService invoiceEventService;

    @Autowired
    public InvoicePaymentCashFlowChangedHandler(InvoiceEventService invoiceEventService) {
        this.invoiceEventService = invoiceEventService;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new InvoiceMapper(),
                new PaymentCommissionMapper()
        );
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        InvoiceEventContext context = generateContext(change, parent);
        return () -> invoiceEventService.saveInvoicePaymentCashFlowEvent(context.getInvoiceEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CASH_FLOW_CHANGED;
    }
}

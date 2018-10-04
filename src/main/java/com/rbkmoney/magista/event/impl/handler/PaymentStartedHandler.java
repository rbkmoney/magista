package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentCommissionMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentMapper;
import com.rbkmoney.magista.service.InvoiceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler extends AbstractInvoiceEventHandler {

    private final InvoiceEventService invoiceEventService;

    @Autowired
    public PaymentStartedHandler(InvoiceEventService invoiceEventService) {
        this.invoiceEventService = invoiceEventService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent event) {
        InvoiceEventContext context = generateContext(change, event);
        return () -> invoiceEventService.saveInvoicePaymentEvent(context.getInvoiceEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_STARTED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new InvoiceMapper(),
                new PaymentMapper(),
                new PaymentCommissionMapper()
        );
    }
}

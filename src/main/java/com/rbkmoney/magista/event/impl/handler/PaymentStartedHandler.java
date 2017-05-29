package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.PaymentCommissionMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentGeoMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentMapper;
import com.rbkmoney.magista.event.impl.processor.CompositeProcessor;
import com.rbkmoney.magista.event.impl.processor.InvoicePaymentEventProcessor;
import com.rbkmoney.magista.event.impl.processor.PaymentProcessor;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.service.InvoiceEventService;
import com.rbkmoney.magista.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler extends AbstractInvoiceEventHandler {

    @Autowired
    GeoProvider geoProvider;

    @Autowired
    PaymentService paymentService;

    @Autowired
    InvoiceEventService invoiceEventService;

    @Override
    public Processor handle(StockEvent event) {
        InvoiceEventContext context = generateContext(event);
        return new CompositeProcessor(
                new PaymentProcessor(paymentService, context.getPayment()),
                new InvoicePaymentEventProcessor(invoiceEventService, context.getInvoiceEventStat())
        );
    }

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_PAYMENT_STARTED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new PaymentMapper(),
                new PaymentCommissionMapper(),
                new PaymentGeoMapper(geoProvider)
        );
    }
}

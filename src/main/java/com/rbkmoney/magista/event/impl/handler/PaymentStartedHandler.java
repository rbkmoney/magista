package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventContext;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.PaymentGeoMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentMapper;
import com.rbkmoney.magista.provider.GeoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler extends AbstractInvoiceEventHandler {

    @Autowired
    GeoProvider geoProvider;

    private List<Mapper> mappers;

    @PostConstruct
    public void init() {
        mappers = Arrays.asList(
                new PaymentMapper(),
                new PaymentGeoMapper(geoProvider)
        );
    }

    @Override
    public EventContext handle(StockEvent value) {
        InvoiceEventContext context = new InvoiceEventContext(value);
        for (Mapper mapper : mappers) {
            context = (InvoiceEventContext) mapper.fill(context);
        }
        return context;
    }

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_PAYMENT_STARTED;
    }

    @Override
    List<Mapper> getMappers() {
        return mappers;
    }
}

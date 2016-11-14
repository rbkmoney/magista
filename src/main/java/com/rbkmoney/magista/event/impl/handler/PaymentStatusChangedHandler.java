package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventContext;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.PaymentStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 10.08.16.
 */
@Component
public class PaymentStatusChangedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private List<Mapper> mappers = Arrays.asList(new Mapper[]{new PaymentStatusMapper()});

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
        return EventType.INVOICE_PAYMENT_STATUS_CHANGED;
    }
}

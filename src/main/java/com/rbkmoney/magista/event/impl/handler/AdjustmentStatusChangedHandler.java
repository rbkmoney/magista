package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.EventMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentAdjustmentStatusMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 21/06/2017.
 */
@Component
public class AdjustmentStatusChangedHandler extends AbstractInvoiceEventHandler  {
    @Override
    public Processor handle(StockEvent event) {
        InvoiceEventContext context = generateContext(event);
        return null;
    }

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new EventMapper(),
                new PaymentAdjustmentStatusMapper()
        );
    }
}

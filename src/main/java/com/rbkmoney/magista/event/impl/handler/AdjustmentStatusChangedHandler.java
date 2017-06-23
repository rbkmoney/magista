package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.EventMapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentAdjustmentStatusMapper;
import com.rbkmoney.magista.event.impl.processor.PaymentAdjustmentEventStatusChangeProcessor;
import com.rbkmoney.magista.service.InvoiceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 21/06/2017.
 */
@Component
public class AdjustmentStatusChangedHandler extends AbstractInvoiceEventHandler {

    @Autowired
    InvoiceEventService invoiceEventService;

    @Override
    public Processor handle(StockEvent event) {
        InvoiceEventContext context = generateContext(event);
        return new PaymentAdjustmentEventStatusChangeProcessor(invoiceEventService, context.getInvoiceEventStat());
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

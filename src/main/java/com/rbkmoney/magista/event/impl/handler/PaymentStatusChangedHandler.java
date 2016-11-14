package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.mapper.PaymentStatusMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 10.08.16.
 */
@Component
public class PaymentStatusChangedHandler extends AbstractInvoiceEventHandler {

    private List<Mapper> mappers = Arrays.asList(new Mapper[]{
            new PaymentStatusMapper()
    });

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_PAYMENT_STATUS_CHANGED;
    }

    @Override
    List<Mapper> getMappers() {
        return mappers;
    }
}

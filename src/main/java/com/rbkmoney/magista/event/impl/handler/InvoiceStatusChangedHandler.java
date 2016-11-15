package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.mapper.InvoiceStatusMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceStatusChangedHandler extends AbstractInvoiceEventHandler {

    List<Mapper> mappers = Arrays.asList(
            new InvoiceStatusMapper()
    );

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_STATUS_CHANGED;
    }

    @Override
    List<Mapper> getMappers() {
        return mappers;
    }
}

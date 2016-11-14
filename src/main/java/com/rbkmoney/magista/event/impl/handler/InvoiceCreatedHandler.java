package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceCreatedHandler extends AbstractInvoiceEventHandler {

    List<Mapper> mappers = Arrays.asList(new Mapper[]{
            new InvoiceMapper()
    });

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_CREATED;
    }


    @Override
    List<Mapper> getMappers() {
        return mappers;
    }
}

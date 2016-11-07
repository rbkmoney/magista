package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceStatusChangedHandler implements Handler<StockEvent, InvoiceStatusChange> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_status_changed.status";

    private Filter filter;

    public InvoiceStatusChangedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public InvoiceStatusChange handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoice();
        Instant changedAt = Instant.from(TemporalConverter.stringToTemporal(event.getCreatedAt()));
        InvoiceStatus status = event.getPayload().getInvoiceEvent().getInvoiceStatusChanged().getStatus();

        InvoiceStatusChange invoiceStatusChange = new InvoiceStatusChange();
        invoiceStatusChange.setEventId(eventId);
        invoiceStatusChange.setInvoiceId(invoiceId);
        invoiceStatusChange.setChangedAt(changedAt);
        invoiceStatusChange.setStatus(status);

        return invoiceStatusChange;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

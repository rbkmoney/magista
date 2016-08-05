package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.repository.InvoiceRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceStatusChangedHandler implements Handler<StockEvent> {

    String path = "source_event.processing_event.payload.invoice_event.invoice_status_changed.status";

    @Autowired
    InvoiceRepository repository;

    @Override
    public void handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        String invoiceId = event.getSource().getInvoice();
        InvoiceStatus status = event.getPayload().getInvoiceEvent().getInvoiceStatusChanged().getStatus();

        repository.changeStatus(invoiceId, status.getSetField());
    }

    @Override
    public Filter getFilter() {
        return new PathConditionFilter(new PathConditionRule(path));
    }
}

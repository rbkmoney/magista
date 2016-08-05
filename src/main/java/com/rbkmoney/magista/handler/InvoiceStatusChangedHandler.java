package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.repository.DaoException;
import com.rbkmoney.magista.repository.InvoiceRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceStatusChangedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_status_changed.status";

    @Autowired
    private InvoiceRepository repository;

    private Filter filter;

    public InvoiceStatusChangedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        String invoiceId = event.getSource().getInvoice();
        InvoiceStatus status = event.getPayload().getInvoiceEvent().getInvoiceStatusChanged().getStatus();
        try {
            repository.changeStatus(invoiceId, status.getSetField());
        } catch (DaoException ex) {
            log.error("Failed to change invoice status", ex);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

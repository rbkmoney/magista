package com.rbkmoney.magista.handler;

import com.rbkmoney.magista.repository.DaoException;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.repository.InvoiceRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;

import java.time.Instant;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceCreatedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_created";

    @Autowired
    private InvoiceRepository repository;

    private Filter filter;

    public InvoiceCreatedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();
        InvoiceCreated invoiceCreated = value.getSourceEvent().getProcessingEvent().getPayload().getInvoiceEvent().getInvoiceCreated();

        Invoice invoice = new Invoice();
        invoice.setId(invoiceCreated.getInvoice().getId());
        invoice.setEventId(eventId);
        //invoice.setShopId(invoiceCreated.getInvoice().get); WAIT PARTY MANAGEMENT
        //invoice.setMerchantId(); WAIT PARTY MANAGEMENT
        invoice.setStatus(invoiceCreated.getInvoice().getStatus().getSetField());
        invoice.setCreatedAt(Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt())));
        invoice.setAmount(invoiceCreated.getInvoice().getCost().getAmount());
        invoice.setCurrencyCode(invoiceCreated.getInvoice().getCost().getCurrency().getSymbolicCode());
        try {
            repository.save(invoice);
        } catch (DaoException ex) {
            log.error("Failed to save invoice", ex);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

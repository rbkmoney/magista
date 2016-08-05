package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.repository.DaoException;
import com.rbkmoney.magista.repository.PaymentRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class PaymentStartedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PaymentRepository repository;

    private Filter filter;

    private String path = "source_event.processing_event.payload.invoice_event.invoice_payment_event.payment";

    public PaymentStartedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        String invoiceId = event.getSource().getInvoice();
        InvoicePayment invoicePayment = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStarted().getPayment();

        Payment payment = new Payment();
        payment.setId(invoicePayment.getId());
        payment.setInvoiceId(invoiceId);
        payment.setStatus(invoicePayment.getStatus().getSetField());
        payment.setCreatedAt(Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt())));

        try {
            repository.save(payment);
        } catch (DaoException ex) {
            log.error("Failed to save payment", ex);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

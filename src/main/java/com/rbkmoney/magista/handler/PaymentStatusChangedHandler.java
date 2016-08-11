package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.repository.DaoException;
import com.rbkmoney.magista.repository.PaymentRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tolkonepiu on 10.08.16.
 */
@Component
public class PaymentStatusChangedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_status_changed.status";

    @Autowired
    private PaymentRepository repository;

    private Filter filter;

    public PaymentStatusChangedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }


    @Override
    public void handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStatusChanged();
        try {
            repository.changeStatus(invoicePaymentStatusChanged.getPaymentId(), invoicePaymentStatusChanged.getStatus());
        } catch (DaoException ex) {
            log.error("Failed to change payment status", ex);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.service.PaymentService;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PaymentService paymentService;

    private Filter filter;

    private String path = "source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_started.payment";

    public PaymentStartedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoice();
        InvoicePayment invoicePayment = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStarted().getPayment();

        paymentService.savePayment(invoiceId, eventId, invoicePayment);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

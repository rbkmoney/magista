package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler implements Handler<StockEvent, Payment> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private Filter filter;

    private String path = "source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_started.payment";

    public PaymentStartedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public Payment handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoice();
        InvoicePayment invoicePayment = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStarted().getPayment();

        Payment payment = new Payment();
        payment.setId(invoicePayment.getId());
        payment.setEventId(eventId);
        payment.setInvoiceId(invoiceId);

        Payer payer = invoicePayment.getPayer();

        ClientInfo clientInfo = payer.getClientInfo();
        payment.setCustomerId(clientInfo.getFingerprint());
        payment.setIp(clientInfo.getIpAddress());

//        try {
//            log.info("Start enrichment");
//            payment.setCityName(geoProvider.getCityName(payment.getIp()));
//        } catch (ProviderException ex) {
//            log.warn("Failed to find city name by ip", ex);
//            payment.setCityName("UNKNOWN");
//        } finally {
//            log.info("Finish enrichment");
//        }

        PaymentTool paymentTool = payer.getPaymentTool();
        payment.setMaskedPan(paymentTool.getBankCard().getMaskedPan());
        payment.setPaymentSystem(paymentTool.getBankCard().getPaymentSystem());

        payment.setStatus(invoicePayment.getStatus().getSetField());

        Cash cost = invoicePayment.getCost();
        payment.setAmount(cost.getAmount());
        payment.setCurrencyCode(cost.getCurrency().getSymbolicCode());

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt()));
        payment.setCreatedAt(createdAt);
        payment.setChangedAt(createdAt);

        payment.setModel(invoicePayment);

        return payment;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

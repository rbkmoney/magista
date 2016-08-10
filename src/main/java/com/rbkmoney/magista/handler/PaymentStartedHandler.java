package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.model.Customer;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.repository.CustomerRepository;
import com.rbkmoney.magista.repository.InvoiceRepository;
import com.rbkmoney.magista.repository.PaymentRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Component
public class PaymentStartedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CustomerRepository customerRepository;

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
        payment.setEventId(event.getId());
        payment.setInvoiceId(invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId);
        payment.setMerchantId(invoice.getMerchantId());
        payment.setShopId(invoice.getShopId());

        Payer payer = invoicePayment.getPayer();

        ClientInfo clientInfo = payer.getClientInfo();
        payment.setCustomerId(clientInfo.getFingerprint());
        payment.setIp(clientInfo.getIpAddress());
        payment.setCityName("");

        PaymentTool paymentTool = payer.getPaymentTool();
        payment.setMaskedPan(paymentTool.getBankCard().getMaskedPan());
        payment.setPaymentSystem(paymentTool.getBankCard().getPaymentSystem());

        payment.setStatus(invoicePayment.getStatus().getSetField());

        Funds cost = invoicePayment.getCost();
        payment.setAmount(cost.getAmount());
        payment.setCurrencyCode(cost.getCurrency().getSymbolicCode());

        payment.setCreatedAt(Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt())));

        paymentRepository.save(payment);

        if (customerRepository.findByIds(payment.getCustomerId(), payment.getShopId(), payment.getMerchantId()) == null) {
            Customer customer = new Customer();
            customer.setId(payment.getCustomerId());
            customer.setShopId(payment.getShopId());
            customer.setMerchantId(payment.getMerchantId());
            customer.setCreatedAt(payment.getCreatedAt());
            customerRepository.save(customer);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}

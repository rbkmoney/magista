package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.dao.CustomerDao;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.model.Customer;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.provider.ProviderException;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Created by tolkonepiu on 22.08.16.
 */
@Service
public class PaymentService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceDao invoiceDao;

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    CustomerDao customerDao;


    public void changePaymentStatus(String paymentId, String invoiceId, long eventId, InvoicePaymentStatus status, Instant changedAt) throws NotFoundException, DataAccessException {
        log.trace("Change payment status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'", paymentId, invoiceId, eventId, status.getSetField().getFieldName());

        try {
            Payment payment = paymentDao.findById(paymentId);
            if (payment == null) {
                throw new NotFoundException(String.format("Payment not found, paymentId='%s', invoiceId='%s', eventId='%d'", paymentId, invoiceId, eventId));
            }

            payment.setStatus(status.getSetField());
            payment.setChangedAt(changedAt);
            payment.getModel().setStatus(status);

            paymentDao.update(payment);
            log.info("Payment status have been changed, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'", paymentId, invoiceId, eventId, status.getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change payment status, paymentId='%s', invoiceId='%s', eventId='%d', invoiceStatus='%s'", paymentId, invoiceId, eventId, status.getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void savePayment(String invoiceId, long eventId, InvoicePayment invoicePayment) throws NotFoundException, StorageException {
        log.trace("Save payment, paymentId='{}', invoiceId='{}', eventId='{}'", invoicePayment.getId(), invoiceId, eventId);

        try {
            Payment payment = new Payment();
            payment.setId(invoicePayment.getId());
            payment.setEventId(eventId);
            payment.setInvoiceId(invoiceId);

            Invoice invoice = invoiceDao.findById(invoiceId);
            if (invoice == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'", invoiceId, eventId));
            }

            payment.setMerchantId(invoice.getMerchantId());
            payment.setShopId(invoice.getShopId());

            Payer payer = invoicePayment.getPayer();

            ClientInfo clientInfo = payer.getClientInfo();
            payment.setCustomerId(clientInfo.getFingerprint());
            payment.setIp(clientInfo.getIpAddress());

            try {
                log.info("Start enrichment");
                //todo: fix
                payment.setCityName("test");
            } catch (ProviderException ex) {
                log.warn("Failed to find city name by ip", ex);
                payment.setCityName("UNKNOWN");
            } finally {
                log.info("Finish enrichment");
            }

            PaymentTool paymentTool = payer.getPaymentTool();
            payment.setMaskedPan(paymentTool.getBankCard().getMaskedPan());
            payment.setPaymentSystem(paymentTool.getBankCard().getPaymentSystem());

            payment.setStatus(invoicePayment.getStatus().getSetField());

            Funds cost = invoicePayment.getCost();
            payment.setAmount(cost.getAmount());
            payment.setCurrencyCode(cost.getCurrency().getSymbolicCode());

            Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt()));
            payment.setCreatedAt(createdAt);
            payment.setChangedAt(createdAt);

            payment.setModel(invoicePayment);

            if (payment.getCustomerId() != null && customerDao.findByIds(payment.getCustomerId(), payment.getShopId(), payment.getMerchantId()) == null) {
                log.trace("Save customer, customerId='{}', paymentId='{}', invoiceId='{}', eventId='{}'", payment.getCustomerId(), invoicePayment.getId(), invoiceId, eventId);
                Customer customer = new Customer();
                customer.setId(payment.getCustomerId());
                customer.setShopId(payment.getShopId());
                customer.setMerchantId(payment.getMerchantId());
                customer.setCreatedAt(payment.getCreatedAt());
                customerDao.insert(customer);
                log.info("New customer have been saved, customerId='{}', invoiceId='{}', eventId='{}'", payment.getId(), invoiceId, eventId);
            }

            paymentDao.insert(payment);
            log.info("Payment have been saved, paymentId='{}', invoiceId='{}', eventId='{}'", invoicePayment.getId(), invoiceId, eventId);

        } catch (DaoException ex) {
            String message = String.format("Failed to save payment, paymentId='%s', invoiceId='%s', eventId='%d'", invoicePayment.getId(), invoiceId, eventId);
            throw new StorageException(message, ex);
        }
    }

}

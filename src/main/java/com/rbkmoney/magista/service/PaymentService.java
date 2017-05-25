package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.magista.dao.CustomerDao;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.model.Customer;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.magista.provider.GeoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * Created by tolkonepiu on 22.08.16.
 */
@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceDao invoiceDao;

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    CustomerDao customerDao;

    public Payment getPaymentByIds(String paymentId, String invoiceId) throws DataAccessException {
        return paymentDao.findById(paymentId, invoiceId);
    }

    public void changePaymentStatus(PaymentStatusChange paymentStatusChange) throws NotFoundException, DataAccessException {
        log.trace("Change payment status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());

        try {
            Payment payment = paymentDao.findById(paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId());
            if (payment == null) {
                throw new NotFoundException(String.format("Payment not found, paymentId='%s', invoiceId='%s', eventId='%d'",
                        paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId()));
            }

            InvoicePaymentStatus status = paymentStatusChange.getStatus();
            payment.setStatus(status.getSetField());
            if (status.isSetFailed()) {
                OperationFailure operationFailure = status.getFailed().getFailure();
                payment.setFailureCode(operationFailure.getCode());
                payment.setFailureDescription(operationFailure.getDescription());
            }
            payment.setChangedAt(paymentStatusChange.getChangedAt());

            paymentDao.update(payment);
            log.info("Payment status have been changed, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                    paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change payment status, paymentId='%s', invoiceId='%s', eventId='%d', invoiceStatus='%s'",
                    paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void savePayment(Payment payment) throws NotFoundException, StorageException {
        log.trace("Save payment, paymentId='{}', invoiceId='{}', eventId='{}'", payment.getId(), payment.getInvoiceId(), payment.getEventId());

        try {
            Invoice invoice = invoiceDao.findById(payment.getInvoiceId());
            if (invoice == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'", payment.getInvoiceId(), payment.getEventId()));
            }

            payment.setMerchantId(invoice.getMerchantId());
            payment.setShopId(invoice.getShopId());

            if (payment.getCustomerId() != null && customerDao.findByIds(payment.getCustomerId(), payment.getShopId(), payment.getMerchantId()) == null) {
                log.trace("Save customer, customerId='{}', paymentId='{}', invoiceId='{}', eventId='{}'", payment.getCustomerId(), payment.getId(), payment.getInvoiceId(), payment.getEventId());
                Customer customer = new Customer();
                customer.setId(payment.getCustomerId());
                customer.setShopId(payment.getShopId());
                customer.setMerchantId(payment.getMerchantId());
                customer.setCreatedAt(payment.getCreatedAt());
                customerDao.insert(customer);
                log.info("New customer have been saved, customerId='{}', invoiceId='{}', eventId='{}'", payment.getId(), payment.getInvoiceId(), payment.getEventId());
            }

            paymentDao.insert(payment);
            log.info("Payment have been saved, paymentId='{}', invoiceId='{}', eventId='{}'", payment.getId(), payment.getInvoiceId(), payment.getEventId());

        } catch (DaoException ex) {
            String message = String.format("Failed to save payment, paymentId='%s', invoiceId='%s', eventId='%d'", payment.getId(), payment.getInvoiceId(), payment.getEventId());
            throw new StorageException(message, ex);
        }
    }

}

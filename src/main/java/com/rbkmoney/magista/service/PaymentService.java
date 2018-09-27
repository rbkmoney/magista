package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    private final PaymentDao paymentDao;

    @Autowired
    public PaymentService(InvoiceDao invoiceDao, PaymentDao paymentDao) {
        this.invoiceDao = invoiceDao;
        this.paymentDao = paymentDao;
    }

    public PaymentData getPaymentData(String invoiceId, String paymentId) {
        //TODO
        PaymentData paymentData = paymentDao.getPaymentData(invoiceId, paymentId);
        if (paymentData == null) {
            throw new NotFoundException();
        }
        return paymentData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void savePayment(PaymentData paymentData, PaymentEvent paymentEvent) throws NotFoundException, StorageException {
        log.info("Trying to save payment, paymentData='{}', paymentEvent='{}'", paymentData, paymentEvent);
        try {
            InvoiceData invoiceData = invoiceDao.getInvoiceData(paymentData.getInvoiceId());
            if (invoiceData == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", paymentData.getInvoiceId()));
            }
            paymentData.setPartyId(invoiceData.getPartyId());
            paymentData.setPartyShopId(invoiceData.getPartyShopId());

            paymentDao.savePaymentData(paymentData);
            paymentDao.savePaymentEvent(paymentEvent);
            log.info("Payment have been saved, paymentData='{}', paymentEvent='{}'", paymentData, paymentEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment, paymentData='%s', paymentEvent='%s'", paymentData, paymentEvent), ex);
        }
    }

    public void savePaymentChange(PaymentEvent paymentEvent) throws NotFoundException, StorageException {
        log.info("Trying to save payment change, paymentEvent='{}'", paymentEvent);
        try {
            PaymentEvent lastPaymentEvent = paymentDao.getPaymentEvent(paymentEvent.getInvoiceId(), paymentEvent.getPaymentId());
            if (lastPaymentEvent == null) {
                throw new NotFoundException(String.format("Payment changes not found, invoiceId='%s', paymentId='%s'", paymentEvent.getInvoiceId(), paymentEvent.getPaymentId()));
            }
            BeanUtil.merge(lastPaymentEvent, paymentEvent, "id");
            paymentDao.savePaymentEvent(paymentEvent);
            log.info("Payment change have been saved, paymentEvent='{}'", paymentEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment change, paymentEvent='%s'", paymentEvent), ex);
        }
    }

}

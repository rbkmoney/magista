package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Map;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceService invoiceService;

    private final PaymentDao paymentDao;

    private final Cache<Map.Entry<String, String>, PaymentData> paymentDataCache;
    private final Cache<Map.Entry<String, String>, PaymentEvent> paymentEventCache;

    @Autowired
    public PaymentService(InvoiceService invoiceService, PaymentDao paymentDao, @Value("${cache.paymentData.size}") int cacheSize) {
        this.invoiceService = invoiceService;
        this.paymentDao = paymentDao;
        this.paymentDataCache = Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .build();
        this.paymentEventCache = Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    public PaymentData getPaymentData(String invoiceId, String paymentId) throws NotFoundException, StorageException {
        return paymentDataCache.get(
                new AbstractMap.SimpleEntry<>(invoiceId, paymentId),
                key -> {
                    try {
                        PaymentData paymentData = paymentDao.getPaymentData(key.getKey(), key.getValue());
                        if (paymentData == null) {
                            throw new NotFoundException(String.format("Payment data not found, invoiceId='%s', paymentId='%s'", key.getKey(), key.getValue()));
                        }
                        return paymentData;
                    } catch (DaoException ex) {
                        throw new StorageException(String.format("Failed to get payment data, invoiceId='%s', paymentId='%s'", key.getKey(), key.getValue()), ex);
                    }
                }
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void savePayment(PaymentData paymentData, PaymentEvent paymentEvent) throws NotFoundException, StorageException {
        log.info("Trying to save payment, paymentData='{}', paymentEvent='{}'", paymentData, paymentEvent);
        try {
            InvoiceData invoiceData = invoiceService.getInvoiceData(paymentData.getInvoiceId());
            paymentData.setPartyId(invoiceData.getPartyId());
            paymentData.setPartyShopId(invoiceData.getPartyShopId());

            paymentDao.savePaymentData(paymentData);
            paymentDataCache.put(new AbstractMap.SimpleEntry<>(paymentData.getInvoiceId(), paymentData.getPaymentId()), paymentData);

            paymentDao.savePaymentEvent(paymentEvent);
            paymentEventCache.put(new AbstractMap.SimpleEntry<>(paymentEvent.getInvoiceId(), paymentEvent.getPaymentId()), paymentEvent);

            log.info("Payment have been saved, paymentData='{}', paymentEvent='{}'", paymentData, paymentEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment, paymentData='%s', paymentEvent='%s'", paymentData, paymentEvent), ex);
        }
    }

    public void savePaymentChange(PaymentEvent paymentEvent) throws NotFoundException, StorageException {
        log.info("Trying to save payment change, paymentEvent='{}'", paymentEvent);
        try {
            PaymentEvent lastPaymentEvent = getLastPaymentChange(paymentEvent.getInvoiceId(), paymentEvent.getPaymentId());
            BeanUtil.merge(lastPaymentEvent, paymentEvent, "id");
            paymentDao.savePaymentEvent(paymentEvent);
            paymentEventCache.put(new AbstractMap.SimpleEntry<>(paymentEvent.getInvoiceId(), paymentEvent.getPaymentId()), paymentEvent);
            log.info("Payment change have been saved, paymentEvent='{}'", paymentEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment change, paymentEvent='%s'", paymentEvent), ex);
        }
    }

    private PaymentEvent getLastPaymentChange(String invoiceId, String paymentId) throws NotFoundException, StorageException {
        return paymentEventCache.get(
                new AbstractMap.SimpleEntry<>(invoiceId, paymentId),
                key -> {
                    try {
                        PaymentEvent lastPaymentEvent = paymentDao.getLastPaymentEvent(key.getKey(), key.getValue());
                        if (lastPaymentEvent == null) {
                            throw new NotFoundException(String.format("Payment changes not found, invoiceId='%s', paymentId='%s'", key.getKey(), key.getValue()));
                        }
                        return lastPaymentEvent;
                    } catch (DaoException ex) {
                        throw new StorageException(String.format("Failed to get last payment change, invoiceId='%s', paymentId='%s'", key.getKey(), key.getValue()), ex);
                    }
                }
        );
    }

}

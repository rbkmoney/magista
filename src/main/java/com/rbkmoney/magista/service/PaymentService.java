package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceService invoiceService;

    private final PaymentDao paymentDao;

    private final Cache<Map.Entry<String, String>, PaymentData> paymentDataCache;

    @Autowired
    public PaymentService(InvoiceService invoiceService, PaymentDao paymentDao, @Value("${cache.paymentData.size}") int cacheSize) {
        this.invoiceService = invoiceService;
        this.paymentDao = paymentDao;
        this.paymentDataCache = Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    public PaymentData getPaymentData(String invoiceId, String paymentId) throws NotFoundException, StorageException {
        return paymentDataCache.get(
                new AbstractMap.SimpleEntry<>(invoiceId, paymentId),
                key -> {
                    try {
                        PaymentData paymentData = paymentDao.get(key.getKey(), key.getValue());
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

    public void savePayment(PaymentData paymentData) throws NotFoundException, StorageException {
        log.info("Trying to save payment, paymentData='{}'", paymentData);
        try {
            switch (paymentData.getEventType()) {
                case INVOICE_PAYMENT_STARTED:
                    if (paymentData.getPartyId() == null) {
                        InvoiceData invoiceData = invoiceService.getInvoiceData(paymentData.getInvoiceId());
                        paymentData.setPartyId(invoiceData.getPartyId());
                        paymentData.setPartyShopId(invoiceData.getPartyShopId());
                    }
                    break;
                default:
                    PaymentData previousPaymentData = getPaymentData(paymentData.getInvoiceId(), paymentData.getPaymentId());
                    BeanUtil.merge(previousPaymentData, paymentData);
                    break;
            }
            paymentDao.save(paymentData);
            paymentDataCache.put(new AbstractMap.SimpleEntry<>(paymentData.getInvoiceId(), paymentData.getPaymentId()), paymentData);

            log.info("Payment have been saved, paymentData='{}'", paymentData);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment, paymentData='%s'", paymentData), ex);
        }
    }

}

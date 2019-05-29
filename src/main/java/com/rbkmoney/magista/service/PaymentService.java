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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService {

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

    public void savePayments(List<PaymentData> paymentEvents) throws NotFoundException, StorageException {
        log.info("Trying to save payment events, size={}", paymentEvents.size());

        List<PaymentData> enrichedPaymentEvents = paymentEvents.stream()
                .map(payment -> {
                    switch (payment.getEventType()) {
                        case INVOICE_PAYMENT_STARTED:
                            if (payment.getPartyId() == null) {
                                InvoiceData invoiceData = invoiceService.getInvoiceData(payment.getInvoiceId());
                                payment.setPartyId(invoiceData.getPartyId());
                                payment.setPartyShopId(invoiceData.getPartyShopId());
                            }
                            return payment;
                        default:
                            PaymentData previousPaymentData = getPaymentData(payment.getInvoiceId(), payment.getPaymentId());
                            BeanUtil.merge(previousPaymentData, payment);
                            return payment;
                    }
                })
                .peek(payment -> paymentDataCache.put(new AbstractMap.SimpleEntry<>(payment.getInvoiceId(), payment.getPaymentId()), payment))
                .collect(Collectors.toList());
        try {
            paymentDao.save(enrichedPaymentEvents);
            log.info("Refund event have been saved, size='{}'", enrichedPaymentEvents.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment events, size=%d", paymentEvents.size()), ex);
        }
    }

}

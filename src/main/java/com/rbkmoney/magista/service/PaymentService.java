package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final InvoiceService invoiceService;

    private final PaymentDao paymentDao;

    private final Cache<Map.Entry<String, String>, PaymentData> paymentDataCache;

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
        Map<String, PaymentData> paymentDataCacheMap = new LinkedHashMap<>();
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
                            PaymentData previousPaymentData = paymentDataCacheMap.computeIfAbsent(
                                    payment.getInvoiceId() + payment.getPaymentId(),
                                    key -> getPaymentData(payment.getInvoiceId(), payment.getPaymentId())
                            );

                            BeanUtil.merge(previousPaymentData, payment);
                            return payment;
                    }
                })
                .peek(payment -> paymentDataCacheMap.put(payment.getInvoiceId() + payment.getPaymentId(), payment))
                .peek(payment -> paymentDataCache.put(new AbstractMap.SimpleEntry<>(payment.getInvoiceId(), payment.getPaymentId()), payment))
                .collect(Collectors.toList());

        List<PaymentData> paymentStartedEvents = enrichedPaymentEvents.stream()
                .filter(paymentData -> paymentData.getEventType() == InvoiceEventType.INVOICE_PAYMENT_STARTED)
                .collect(Collectors.toList());
        List<PaymentData> updatedPayments = new ArrayList<>(paymentDataCacheMap.values());
        updatedPayments.removeAll(paymentStartedEvents);

        try {
            paymentDao.insert(paymentStartedEvents);
            paymentDao.update(updatedPayments);
            log.info("Payment events have been saved, batchSize={}, insertsCount={}, updatesCount={}", paymentEvents.size(), paymentStartedEvents.size(), updatedPayments.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save payment events, batchSize=%d, insertsCount=%d, updatesCount=%d", paymentEvents.size(), paymentStartedEvents.size(), updatedPayments.size()), ex);
        }
    }

}

package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AdjustmentDao;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAdjustmentService {

    private final AdjustmentDao adjustmentDao;

    private final PaymentService paymentService;

    public AdjustmentData getAdjustment(String invoiceId, String paymentId, String adjustmentId) throws StorageException {
        try {
            AdjustmentData adjustment = adjustmentDao.get(invoiceId, paymentId, adjustmentId);
            if (adjustment == null) {
                throw new NotFoundException(String.format("Adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s'", invoiceId, paymentId, adjustmentId));
            }
            return adjustment;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get adjustment, invoiceId='%s', paymentId='%s', adjustmentId='%s'", invoiceId, paymentId, adjustmentId), ex);
        }
    }

    public void saveAdjustments(List<AdjustmentData> adjustments) throws NotFoundException, StorageException {
        log.info("Trying to save adjustment events, size={}", adjustments.size());

        Map<String, AdjustmentData> adjustmentDataCacheMap = new HashMap<>();
        List<AdjustmentData> enrichedAdjustmentEvents = adjustments.stream()
                .map(adjustment -> {
                    switch (adjustment.getEventType()) {
                        case INVOICE_PAYMENT_ADJUSTMENT_CREATED:
                            PaymentData paymentData = paymentService.getPaymentData(adjustment.getInvoiceId(), adjustment.getPaymentId());
                            adjustment.setPartyId(paymentData.getPartyId().toString());
                            adjustment.setPartyShopId(paymentData.getPartyShopId());
                            return adjustment;
                        default:
                            AdjustmentData previousAdjustmentEvent = adjustmentDataCacheMap.computeIfAbsent(
                                    adjustment.getInvoiceId() + adjustment.getPaymentId() + adjustment.getAdjustmentId(),
                                    key -> getAdjustment(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId())
                            );
                            BeanUtil.merge(previousAdjustmentEvent, adjustment);
                            return adjustment;
                    }
                })
                .peek(adjustmentData -> adjustmentDataCacheMap.put(adjustmentData.getInvoiceId() + adjustmentData.getPaymentId() + adjustmentData.getAdjustmentId(), adjustmentData))
                .collect(Collectors.toList());

        List<PaymentData> adjustedPaymentEvents = enrichedAdjustmentEvents.stream()
                .filter(adjustmentData -> adjustmentData.getAdjustmentStatus() == AdjustmentStatus.captured)
                .map(adjustmentData -> {
                            PaymentData paymentData = new PaymentData();
                            paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED);
                            paymentData.setEventId(adjustmentData.getEventId());
                            paymentData.setEventCreatedAt(adjustmentData.getEventCreatedAt());
                            paymentData.setInvoiceId(adjustmentData.getInvoiceId());
                            paymentData.setPaymentId(adjustmentData.getPaymentId());
                            paymentData.setPaymentFee(adjustmentData.getAdjustmentFee());
                            paymentData.setPaymentProviderFee(adjustmentData.getAdjustmentProviderFee());
                            paymentData.setPaymentExternalFee(adjustmentData.getAdjustmentExternalFee());
                            paymentData.setPaymentDomainRevision(adjustmentData.getAdjustmentDomainRevision());
                            return paymentData;
                        }
                )
                .collect(Collectors.toList());

        try {
            adjustmentDao.save(enrichedAdjustmentEvents);
            paymentService.savePayments(adjustedPaymentEvents);
            log.info("Adjustment events have been saved, size={}", enrichedAdjustmentEvents.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save adjustment events, size=%d", adjustments.size()), ex);
        }
    }

}

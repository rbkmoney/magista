package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AdjustmentDao;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

        List<AdjustmentData> enrichedAdjustmentEvents = adjustments.stream()
                .map(adjustment -> {
                    switch (adjustment.getEventType()) {
                        case INVOICE_PAYMENT_ADJUSTMENT_CREATED:
                            PaymentData paymentData = paymentService.getPaymentData(adjustment.getInvoiceId(), adjustment.getPaymentId());
                            adjustment.setPartyId(paymentData.getPartyId().toString());
                            adjustment.setPartyShopId(paymentData.getPartyShopId());
                            return adjustment;
                        default:
                            AdjustmentData previousAdjustmentEvent = getAdjustment(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId());
                            BeanUtil.merge(previousAdjustmentEvent, adjustment);
                            return adjustment;
                    }
                })
                .collect(Collectors.toList());

        try {
            adjustmentDao.save(enrichedAdjustmentEvents);
            log.info("Adjustment events have been saved, size='{}'", enrichedAdjustmentEvents.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save adjustment events, size=%d", adjustments.size()), ex);
        }
    }

}

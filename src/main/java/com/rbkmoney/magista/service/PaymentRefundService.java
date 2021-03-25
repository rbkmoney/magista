package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.RefundDao;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
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
public class PaymentRefundService {

    private final RefundDao refundDao;

    private final PaymentService paymentService;

    public RefundData getRefund(String invoiceId, String paymentId, String refundId)
            throws NotFoundException, StorageException {
        try {
            RefundData refund = refundDao.get(invoiceId, paymentId, refundId);
            if (refund == null) {
                throw new NotFoundException(
                        String.format("Refund not found, invoiceId='%s', paymentId='%s', refundId='%s'", invoiceId,
                                paymentId, refundId));
            }
            return refund;
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to get refund, invoiceId='%s', paymentId='%s', refundId='%s'", invoiceId,
                            paymentId, refundId), ex);
        }
    }

    public void saveRefunds(List<RefundData> refundEvents) throws NotFoundException, StorageException {
        log.info("Trying to save refund events, size={}", refundEvents.size());
        Map<String, RefundData> refundDataCacheMap = new HashMap<>();
        List<RefundData> enrichedRefundEvents = refundEvents.stream()
                .map(refund -> {
                    switch (refund.getEventType()) {
                        case INVOICE_PAYMENT_REFUND_CREATED:
                            PaymentData paymentData =
                                    paymentService.getPaymentData(refund.getInvoiceId(), refund.getPaymentId());
                            refund.setPartyId(paymentData.getPartyId().toString());
                            refund.setPartyShopId(paymentData.getPartyShopId());
                            if (refund.getRefundAmount() == null) {
                                refund.setRefundAmount(paymentData.getPaymentAmount());
                                refund.setRefundCurrencyCode(paymentData.getPaymentCurrencyCode());
                            }
                            return refund;
                        default:
                            RefundData previousRefund = refundDataCacheMap.computeIfAbsent(
                                    refund.getInvoiceId() + refund.getPaymentId() + refund.getRefundId(),
                                    key -> getRefund(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId())
                            );
                            BeanUtil.merge(previousRefund, refund);
                            return refund;
                    }
                })
                .peek(refundData -> refundDataCacheMap
                        .put(refundData.getInvoiceId() + refundData.getPaymentId() + refundData.getRefundId(),
                                refundData))
                .collect(Collectors.toList());

        try {
            refundDao.save(enrichedRefundEvents);
            log.info("Refund events have been saved, size={}", enrichedRefundEvents.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save refund events, size=%d", refundEvents.size()), ex);
        }
    }

}

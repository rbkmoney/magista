package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.RefundDao;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentRefundService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RefundDao refundDao;

    private final PaymentService paymentService;

    public PaymentRefundService(RefundDao refundDao, PaymentService paymentService) {
        this.refundDao = refundDao;
        this.paymentService = paymentService;
    }

    public Refund getRefund(String invoiceId, String paymentId, String refundId) throws NotFoundException, StorageException {
        try {
            Refund refund = refundDao.get(invoiceId, paymentId, refundId);
            if (refund == null) {
                throw new NotFoundException(String.format("Refund not found, invoiceId='%s', paymentId='%s', refundId='%s'", invoiceId, paymentId, refundId));
            }
            return refund;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get refund, invoiceId='%s', paymentId='%s', refundId='%s'", invoiceId, paymentId, refundId), ex);
        }
    }

    public void savePaymentRefund(Refund refund) throws NotFoundException, StorageException {
        log.info("Trying to save refund event, eventType='{}', invoiceId='{}', paymentId='{}', refundId='{}'",
                refund.getEventType(), refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());
        switch (refund.getEventType()) {
            case INVOICE_PAYMENT_REFUND_CREATED:
                PaymentData paymentData = paymentService.getPaymentData(refund.getInvoiceId(), refund.getPaymentId());
                PaymentEvent paymentEvent = paymentService.getLastPaymentChange(refund.getInvoiceId(), refund.getPaymentId());
                refund.setPartyId(paymentData.getPartyId().toString());
                refund.setPartyShopId(paymentData.getPartyShopId());
                if (refund.getRefundAmount() == null) {
                    refund.setRefundAmount(paymentEvent.getPaymentAmount());
                    refund.setRefundCurrencyCode(paymentEvent.getPaymentCurrencyCode());
                }
                break;
            case INVOICE_PAYMENT_REFUND_STATUS_CHANGED:
                Refund previousRefundEvent = getRefund(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());
                BeanUtil.merge(previousRefundEvent, refund, "id");
                break;
        }

        try {
            refundDao.save(refund);
            log.info("Refund event have been saved, refund='{}'", refund);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save refund, refund='%s'", refund), ex);
        }
    }

}

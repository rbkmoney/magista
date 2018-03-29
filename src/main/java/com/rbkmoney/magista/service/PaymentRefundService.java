package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.RefundDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentRefundService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RefundDao refundDao;

    private final InvoiceEventService invoiceEventService;

    public PaymentRefundService(RefundDao refundDao, InvoiceEventService invoiceEventService) {
        this.refundDao = refundDao;
        this.invoiceEventService = invoiceEventService;
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
                InvoiceEventStat paymentEventStat = invoiceEventService.getInvoicePaymentEventByIds(refund.getInvoiceId(), refund.getPaymentId());
                refund.setPartyId(paymentEventStat.getPartyId());
                refund.setPartyShopId(paymentEventStat.getPartyShopId());
                refund.setPartyContractId(paymentEventStat.getPartyContractId());
                break;
            case INVOICE_PAYMENT_REFUND_STATUS_CHANGED:
                Refund previousRefundEvent = getRefund(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());

                refund.setPartyId(previousRefundEvent.getPartyId());
                refund.setPartyShopId(previousRefundEvent.getPartyShopId());
                refund.setPartyContractId(previousRefundEvent.getPartyContractId());
                refund.setRefundCreatedAt(previousRefundEvent.getRefundCreatedAt());
                refund.setRefundCurrencyCode(previousRefundEvent.getRefundCurrencyCode());
                refund.setRefundAmount(previousRefundEvent.getRefundAmount());
                refund.setRefundFee(previousRefundEvent.getRefundFee());
                refund.setRefundExternalFee(previousRefundEvent.getRefundExternalFee());
                refund.setRefundProviderFee(previousRefundEvent.getRefundProviderFee());
                refund.setRefundReason(previousRefundEvent.getRefundReason());
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

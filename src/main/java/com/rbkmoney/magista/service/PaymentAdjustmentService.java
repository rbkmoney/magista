package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AdjustmentDao;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
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
public class PaymentAdjustmentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdjustmentDao adjustmentDao;

    private final PaymentService paymentService;

    @Autowired
    public PaymentAdjustmentService(AdjustmentDao adjustmentDao, PaymentService paymentService) {
        this.adjustmentDao = adjustmentDao;
        this.paymentService = paymentService;
    }

    public Adjustment getAdjustment(String invoiceId, String paymentId, String adjustmentId) throws StorageException {
        try {
            Adjustment adjustment = adjustmentDao.get(invoiceId, paymentId, adjustmentId);
            if (adjustment == null) {
                throw new NotFoundException(String.format("Adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s'", invoiceId, paymentId, adjustmentId));
            }
            return adjustment;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get adjustment, invoiceId='%s', paymentId='%s', adjustmentId='%s'", invoiceId, paymentId, adjustmentId), ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void savePaymentAdjustment(Adjustment adjustment) throws NotFoundException, StorageException {
        log.info("Trying to save adjustment event, eventType='{}', invoiceId='{}', paymentId='{}', adjustmentId='{}'",
                adjustment.getEventType(), adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId());
        switch (adjustment.getEventType()) {
            case INVOICE_PAYMENT_ADJUSTMENT_CREATED:
                PaymentData paymentData = paymentService.getPaymentData(adjustment.getInvoiceId(), adjustment.getPaymentId());
                adjustment.setPartyId(paymentData.getPartyId().toString());
                adjustment.setPartyShopId(paymentData.getPartyShopId());
                break;
            case INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED:
                Adjustment previousAdjustmentEvent = getAdjustment(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId());
                BeanUtil.merge(previousAdjustmentEvent, adjustment, "id");
                break;
        }

        try {
            adjustmentDao.save(adjustment);
            log.info("Adjustment event have been saved, adjustment='{}'", adjustment);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save adjustment, adjustment='%s'", adjustment), ex);
        }
    }

}

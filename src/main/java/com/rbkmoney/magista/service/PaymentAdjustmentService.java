package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AdjustmentDao;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
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

    private final InvoiceEventService invoiceEventService;

    private final PaymentService paymentService;

    @Autowired
    public PaymentAdjustmentService(AdjustmentDao adjustmentDao, InvoiceEventService invoiceEventService, PaymentService paymentService) {
        this.adjustmentDao = adjustmentDao;
        this.invoiceEventService = invoiceEventService;
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
                InvoiceEventStat paymentEventStat = invoiceEventService.getInvoicePaymentEventByIds(adjustment.getInvoiceId(), adjustment.getPaymentId());
                adjustment.setPartyId(paymentEventStat.getPartyId());
                adjustment.setPartyShopId(paymentEventStat.getPartyShopId());
                adjustment.setPartyContractId(paymentEventStat.getPartyContractId());
                break;
            case INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED:
                Adjustment previousAdjustmentEvent = getAdjustment(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId());
                adjustment.setPartyId(previousAdjustmentEvent.getPartyId());
                adjustment.setPartyShopId(previousAdjustmentEvent.getPartyShopId());
                adjustment.setPartyContractId(previousAdjustmentEvent.getPartyContractId());
                adjustment.setAdjustmentCreatedAt(previousAdjustmentEvent.getAdjustmentCreatedAt());
                adjustment.setAdjustmentFee(previousAdjustmentEvent.getAdjustmentFee());
                adjustment.setAdjustmentExternalFee(previousAdjustmentEvent.getAdjustmentExternalFee());
                adjustment.setAdjustmentProviderFee(previousAdjustmentEvent.getAdjustmentProviderFee());
                adjustment.setAdjustmentReason(previousAdjustmentEvent.getAdjustmentReason());
                adjustment.setAdjustmentDomainRevision(previousAdjustmentEvent.getAdjustmentDomainRevision());
                break;
        }

        if (adjustment.getAdjustmentStatus() == AdjustmentStatus.captured) {
            InvoiceEventStat paymentEventStat = invoiceEventService.getInvoicePaymentEventByIds(adjustment.getInvoiceId(), adjustment.getPaymentId());
            paymentEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED);
            paymentEventStat.setId(null);
            paymentEventStat.setPaymentFee(adjustment.getAdjustmentFee());
            paymentEventStat.setPaymentExternalFee(adjustment.getAdjustmentExternalFee());
            paymentEventStat.setPaymentProviderFee(adjustment.getAdjustmentProviderFee());
            paymentEventStat.setPaymentDomainRevision(adjustment.getAdjustmentDomainRevision());
            invoiceEventService.saveInvoicePaymentEvent(paymentEventStat);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED);
            paymentEvent.setEventId(adjustment.getEventId());
            paymentEvent.setEventCreatedAt(adjustment.getEventCreatedAt());
            paymentEvent.setInvoiceId(adjustment.getInvoiceId());
            paymentEvent.setPaymentId(adjustment.getPaymentId());
            paymentEvent.setPaymentStatus(paymentEventStat.getPaymentStatus());
            paymentEvent.setPaymentOperationFailureClass(paymentEventStat.getPaymentOperationFailureClass());
            paymentEvent.setPaymentExternalFailure(paymentEventStat.getPaymentExternalFailure());
            paymentEvent.setPaymentExternalFailureReason(paymentEventStat.getPaymentExternalFailureReason());
            paymentEvent.setPaymentFee(adjustment.getAdjustmentFee());
            paymentEvent.setPaymentProviderFee(adjustment.getAdjustmentProviderFee());
            paymentEvent.setPaymentExternalFee(adjustment.getAdjustmentExternalFee());
            paymentEvent.setPaymentDomainRevision(adjustment.getAdjustmentDomainRevision());
            paymentService.savePaymentChange(paymentEvent);
        }

        try {
            adjustmentDao.save(adjustment);
            log.info("Adjustment event have been saved, adjustment='{}'", adjustment);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save adjustment, adjustment='%s'", adjustment), ex);
        }
    }

}

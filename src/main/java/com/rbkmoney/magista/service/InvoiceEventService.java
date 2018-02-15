package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.AdjustmentException;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class InvoiceEventService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceEventDao invoiceEventDao;

    @Autowired
    public InvoiceEventService(InvoiceEventDao invoiceEventDao) {
        this.invoiceEventDao = invoiceEventDao;
    }

    public Optional<Long> getLastEventId() throws StorageException {
        try {
            return Optional.ofNullable(invoiceEventDao.getLastEventId());
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last event id", ex);
        }
    }

    public InvoiceEventStat getInvoiceEventById(String invoiceId, InvoiceEventType eventType) throws NotFoundException {
        InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoiceId);
        if (invoiceEvent == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventType='%s'", invoiceId, eventType));
        }
        return invoiceEvent;
    }

    public InvoiceEventStat getInvoicePaymentEventByIds(String invoiceId, String paymentId, InvoiceEventType eventType) throws NotFoundException {
        InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByIds(invoiceId, paymentId);
        if (invoicePaymentEvent == null) {
            throw new NotFoundException(String.format("Invoice payment not found, invoiceId='%s', paymentId='%s', eventType='%s'", invoiceId, paymentId, eventType));
        }
        return invoicePaymentEvent;
    }

    public InvoiceEventStat getInvoicePaymentAdjustmentEventByIds(String invoiceId, String paymentId, String adjustmentId, InvoiceEventType eventType) throws NotFoundException {
        InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findAdjustmentByIds(invoiceId, paymentId, adjustmentId);
        if (invoicePaymentEvent == null) {
            throw new NotFoundException(String.format("Invoice payment adjustment not found, invoiceId='%s', paymentId='%s', adjustmentId='%s', eventType='%s'", invoiceId, paymentId, adjustmentId, eventType));
        }
        return invoicePaymentEvent;
    }

    public InvoiceEventStat getInvoicePaymentRefundEventByIds(String invoiceId, String paymentId, String refundId, InvoiceEventType eventType) throws NotFoundException {
        InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findRefundByIds(invoiceId, paymentId, refundId);
        if (invoicePaymentEvent == null) {
            throw new NotFoundException(String.format("Invoice payment refund not found, invoiceId='%s', paymentId='%s', refundId='%s', eventType='%s'", invoiceId, paymentId, refundId, eventType));
        }
        return invoicePaymentEvent;
    }

    public void changeInvoiceEventStatus(InvoiceEventStat invoiceStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice event status, invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoiceStatusEvent.getInvoiceId(), invoiceStatusEvent.getEventId(), invoiceStatusEvent.getInvoiceStatus());

        try {
            InvoiceEventStat invoiceEvent = getInvoiceEventById(
                    invoiceStatusEvent.getInvoiceId(),
                    invoiceStatusEvent.getEventType()
            );

            invoiceEvent.setId(invoiceStatusEvent.getId());
            invoiceEvent.setEventCategory(invoiceStatusEvent.getEventCategory());
            invoiceEvent.setEventType(invoiceStatusEvent.getEventType());
            invoiceEvent.setEventId(invoiceStatusEvent.getEventId());
            invoiceEvent.setEventCreatedAt(invoiceStatusEvent.getEventCreatedAt());

            invoiceEvent.setInvoiceStatus(invoiceStatusEvent.getInvoiceStatus());
            invoiceEvent.setInvoiceStatusDetails(invoiceStatusEvent.getInvoiceStatusDetails());

            invoiceEventDao.insert(invoiceEvent);
            log.info("Invoice event status have been changed, invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                    invoiceEvent.getInvoiceId(), invoiceEvent.getEventId(), invoiceEvent.getInvoiceStatus());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice event status, invoiceId='%s', eventId='%d', invoiceStatus='%s'",
                    invoiceStatusEvent.getInvoiceId(), invoiceStatusEvent.getEventId(), invoiceStatusEvent.getInvoiceStatus());
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoicePaymentAdjustment(InvoiceEventStat invoiceAdjustmentEvent) throws NotFoundException, StorageException {
        log.debug("Save adjustment event, adjustmentId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                invoiceAdjustmentEvent.getPaymentAdjustmentId(), invoiceAdjustmentEvent.getPaymentId(), invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getEventId());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoiceAdjustmentEvent.getInvoiceId(),
                    invoiceAdjustmentEvent.getPaymentId(),
                    invoiceAdjustmentEvent.getEventType()
            );

            invoicePaymentEvent.setId(invoiceAdjustmentEvent.getId());
            invoicePaymentEvent.setEventCategory(invoiceAdjustmentEvent.getEventCategory());
            invoicePaymentEvent.setEventType(invoiceAdjustmentEvent.getEventType());
            invoicePaymentEvent.setEventId(invoiceAdjustmentEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoiceAdjustmentEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentAdjustmentId(invoiceAdjustmentEvent.getPaymentAdjustmentId());
            invoicePaymentEvent.setPaymentAdjustmentReason(invoiceAdjustmentEvent.getPaymentAdjustmentReason());
            invoicePaymentEvent.setPaymentAdjustmentStatus(invoiceAdjustmentEvent.getPaymentAdjustmentStatus());
            invoicePaymentEvent.setPaymentAdjustmentStatusCreatedAt(invoiceAdjustmentEvent.getPaymentAdjustmentStatusCreatedAt());
            invoicePaymentEvent.setPaymentAdjustmentCreatedAt(invoiceAdjustmentEvent.getPaymentAdjustmentCreatedAt());
            invoicePaymentEvent.setPaymentAdjustmentAmount(invoiceAdjustmentEvent.getPaymentAdjustmentAmount());
            invoicePaymentEvent.setPaymentAdjustmentFee(invoiceAdjustmentEvent.getPaymentAdjustmentFee());
            invoicePaymentEvent.setPaymentAdjustmentProviderFee(invoiceAdjustmentEvent.getPaymentAdjustmentProviderFee());
            invoicePaymentEvent.setPaymentAdjustmentExternalFee(invoiceAdjustmentEvent.getPaymentAdjustmentExternalFee());

            invoiceEventDao.insert(invoicePaymentEvent);
            log.info("Adjustment event have been saved, event='{}'", invoicePaymentEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save adjustment event, paymentId='%s', invoiceId='%s', eventId='%d'",
                    invoiceAdjustmentEvent.getPaymentId(), invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getEventId());
            throw new StorageException(message, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeInvoicePaymentAdjustmentStatus(InvoiceEventStat adjustmentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change adjustment event status, adjustmentId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                adjustmentStatusEvent.getPaymentAdjustmentId(), adjustmentStatusEvent.getPaymentId(), adjustmentStatusEvent.getInvoiceId(), adjustmentStatusEvent.getEventId());

        try {
            InvoiceEventStat invoicePaymentAdjustmentEvent = getInvoicePaymentAdjustmentEventByIds(
                    adjustmentStatusEvent.getInvoiceId(),
                    adjustmentStatusEvent.getPaymentId(),
                    adjustmentStatusEvent.getPaymentAdjustmentId(),
                    adjustmentStatusEvent.getEventType()
            );

            invoicePaymentAdjustmentEvent.setId(adjustmentStatusEvent.getId());
            invoicePaymentAdjustmentEvent.setEventCategory(adjustmentStatusEvent.getEventCategory());
            invoicePaymentAdjustmentEvent.setEventType(adjustmentStatusEvent.getEventType());
            invoicePaymentAdjustmentEvent.setEventId(adjustmentStatusEvent.getEventId());
            invoicePaymentAdjustmentEvent.setEventCreatedAt(adjustmentStatusEvent.getEventCreatedAt());

            invoicePaymentAdjustmentEvent.setPaymentAdjustmentStatus(adjustmentStatusEvent.getPaymentAdjustmentStatus());
            invoicePaymentAdjustmentEvent.setPaymentAdjustmentStatusCreatedAt(adjustmentStatusEvent.getPaymentAdjustmentStatusCreatedAt());

            invoiceEventDao.insert(invoicePaymentAdjustmentEvent);

            log.info("Adjustment event status have been changed, invoiceId='{}', paymentId='{}', paymentAdjustmentId='{}', eventId='{}', AdjustmentStatus='{}'",
                    invoicePaymentAdjustmentEvent.getInvoiceId(),
                    invoicePaymentAdjustmentEvent.getPaymentId(),
                    invoicePaymentAdjustmentEvent.getPaymentAdjustmentId(),
                    invoicePaymentAdjustmentEvent.getEventId(),
                    invoicePaymentAdjustmentEvent.getPaymentAdjustmentStatus());

            if (invoicePaymentAdjustmentEvent.getPaymentAdjustmentStatus() == AdjustmentStatus.captured) {
                InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                        invoicePaymentAdjustmentEvent.getInvoiceId(),
                        invoicePaymentAdjustmentEvent.getPaymentId(),
                        adjustmentStatusEvent.getEventType()
                );

                invoicePaymentEvent.setId(invoicePaymentAdjustmentEvent.getId());
                invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED);
                invoicePaymentEvent.setPaymentAmount(invoicePaymentAdjustmentEvent.getPaymentAdjustmentAmount());
                invoicePaymentEvent.setPaymentFee(invoicePaymentAdjustmentEvent.getPaymentAdjustmentFee());
                invoicePaymentEvent.setPaymentProviderFee(invoicePaymentAdjustmentEvent.getPaymentAdjustmentProviderFee());
                invoicePaymentEvent.setPaymentExternalFee(invoicePaymentAdjustmentEvent.getPaymentAdjustmentExternalFee());
                invoiceEventDao.insert(invoicePaymentEvent);
                log.info("Payment event have been adjusted, invoiceId='{}', paymentId='{}', eventId='{}'",
                        invoicePaymentAdjustmentEvent.getInvoiceId(),
                        invoicePaymentAdjustmentEvent.getPaymentId(),
                        invoicePaymentAdjustmentEvent.getEventId());

            }
        } catch (DaoException ex) {
            String message = String.format("Failed to change adjustment event status, paymentId='%s', invoiceId='%s', eventId='%d'",
                    adjustmentStatusEvent.getPaymentId(), adjustmentStatusEvent.getInvoiceId(), adjustmentStatusEvent.getEventId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentStatus(InvoiceEventStat invoicePaymentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId(), invoicePaymentStatusEvent.getPaymentStatus());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoicePaymentStatusEvent.getInvoiceId(),
                    invoicePaymentStatusEvent.getPaymentId(),
                    invoicePaymentStatusEvent.getEventType()
            );

            invoicePaymentEvent.setId(invoicePaymentStatusEvent.getId());
            invoicePaymentEvent.setEventType(invoicePaymentStatusEvent.getEventType());
            invoicePaymentEvent.setEventId(invoicePaymentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoicePaymentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentStatus(invoicePaymentStatusEvent.getPaymentStatus());

            invoicePaymentEvent.setPaymentFailureClass(invoicePaymentStatusEvent.getPaymentFailureClass());
            invoicePaymentEvent.setPaymentExternalFailureCode(invoicePaymentStatusEvent.getPaymentExternalFailureCode());
            invoicePaymentEvent.setPaymentExternalFailureDescription(invoicePaymentStatusEvent.getPaymentExternalFailureDescription());

            invoiceEventDao.insert(invoicePaymentEvent);
            log.info("Invoice payment event status have been changed, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                    invoicePaymentEvent.getPaymentId(), invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId(), invoicePaymentEvent.getPaymentStatus());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice payment event status, paymentId='%s', invoiceId='%s', eventId='%d', invoiceStatus='%s'",
                    invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId(), invoicePaymentStatusEvent.getPaymentStatus());
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoiceEvent(InvoiceEventStat invoiceEvent) throws StorageException {
        log.debug("Save invoice event, event='{}'", invoiceEvent);

        try {
            invoiceEventDao.insert(invoiceEvent);
            log.info("Invoice event have been saved, event='{}'", invoiceEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice event, event='%s'", invoiceEvent);
            throw new StorageException(message, ex);
        }

    }

    public void saveInvoicePaymentEvent(InvoiceEventStat invoicePaymentEvent) throws NotFoundException, StorageException {
        log.debug("Save invoice payment event, event='{}'", invoicePaymentEvent);

        try {
            InvoiceEventStat invoiceEvent = getInvoiceEventById(
                    invoicePaymentEvent.getInvoiceId(),
                    invoicePaymentEvent.getEventType()
            );

            invoicePaymentEvent.setInvoiceProduct(invoiceEvent.getInvoiceProduct());
            invoicePaymentEvent.setInvoiceDescription(invoiceEvent.getInvoiceDescription());
            invoicePaymentEvent.setInvoiceCurrencyCode(invoiceEvent.getInvoiceCurrencyCode());
            invoicePaymentEvent.setInvoiceTemplateId(invoiceEvent.getInvoiceTemplateId());
            invoicePaymentEvent.setInvoiceStatus(invoiceEvent.getInvoiceStatus());
            invoicePaymentEvent.setInvoiceStatusDetails(invoiceEvent.getInvoiceStatusDetails());
            invoicePaymentEvent.setInvoiceAmount(invoiceEvent.getInvoiceAmount());
            invoicePaymentEvent.setInvoiceCreatedAt(invoiceEvent.getInvoiceCreatedAt());
            invoicePaymentEvent.setInvoiceDue(invoiceEvent.getInvoiceDue());
            invoicePaymentEvent.setInvoiceContext(invoiceEvent.getInvoiceContext());

            invoicePaymentEvent.setPartyId(invoiceEvent.getPartyId());
            invoicePaymentEvent.setPartyEmail(invoiceEvent.getPartyEmail());
            invoicePaymentEvent.setPartyContractId(invoiceEvent.getPartyContractId());
            invoicePaymentEvent.setPartyContractInn(invoiceEvent.getPartyContractInn());
            invoicePaymentEvent.setPartyContractRegisteredNumber(invoiceEvent.getPartyContractRegisteredNumber());

            invoicePaymentEvent.setPartyShopId(invoiceEvent.getPartyShopId());
            invoicePaymentEvent.setPartyShopCategoryId(invoiceEvent.getPartyShopCategoryId());
            invoicePaymentEvent.setPartyShopName(invoiceEvent.getPartyShopName());
            invoicePaymentEvent.setPartyShopDescription(invoiceEvent.getPartyShopDescription());
            invoicePaymentEvent.setPartyShopPayoutToolId(invoiceEvent.getPartyShopPayoutToolId());
            invoicePaymentEvent.setPartyShopUrl(invoiceEvent.getPartyShopUrl());

            invoiceEventDao.insert(invoicePaymentEvent);
            log.info("Invoice payment event have been saved, event='{}'", invoicePaymentEvent);

        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice payment event, event='%s'", invoicePaymentEvent);
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoicePaymentRefund(InvoiceEventStat refundEventStat) throws StorageException {
        log.debug("Save invoice payment refund event, refundId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                refundEventStat.getPaymentRefundId(), refundEventStat.getPaymentId(), refundEventStat.getInvoiceId(), refundEventStat.getEventId());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    refundEventStat.getInvoiceId(),
                    refundEventStat.getPaymentId(),
                    refundEventStat.getEventType()
            );

            invoicePaymentEvent.setId(refundEventStat.getId());
            invoicePaymentEvent.setEventCategory(refundEventStat.getEventCategory());
            invoicePaymentEvent.setEventType(refundEventStat.getEventType());
            invoicePaymentEvent.setPaymentRefundId(refundEventStat.getPaymentRefundId());
            invoicePaymentEvent.setPaymentRefundReason(refundEventStat.getPaymentRefundReason());
            invoicePaymentEvent.setPaymentRefundStatus(refundEventStat.getPaymentRefundStatus());
            invoicePaymentEvent.setPaymentRefundCreatedAt(refundEventStat.getPaymentRefundCreatedAt());
            invoicePaymentEvent.setPaymentRefundFee(refundEventStat.getPaymentRefundFee());
            invoicePaymentEvent.setPaymentRefundExternalFee(refundEventStat.getPaymentRefundExternalFee());
            invoicePaymentEvent.setPaymentRefundProviderFee(refundEventStat.getPaymentRefundProviderFee());

            invoiceEventDao.insert(invoicePaymentEvent);
            log.info("Invoice payment refund event have been changed, refundId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                    invoicePaymentEvent.getPaymentRefundId(), invoicePaymentEvent.getPaymentId(), invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice payment event status, refundId='%s', paymentId='%s', invoiceId='%s', eventId='%d'",
                    refundEventStat.getPaymentRefundId(), refundEventStat.getPaymentId(), refundEventStat.getInvoiceId(), refundEventStat.getEventId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentRefundStatus(InvoiceEventStat refundStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                refundStatusEvent.getPaymentId(), refundStatusEvent.getInvoiceId(), refundStatusEvent.getEventId(), refundStatusEvent.getPaymentStatus());
        try {
            InvoiceEventStat invoicePaymentRefundEvent = getInvoicePaymentRefundEventByIds(
                    refundStatusEvent.getInvoiceId(),
                    refundStatusEvent.getPaymentId(),
                    refundStatusEvent.getPaymentRefundId(),
                    refundStatusEvent.getEventType()
            );

            invoicePaymentRefundEvent.setPaymentRefundStatus(refundStatusEvent.getPaymentRefundStatus());

            invoiceEventDao.insert(invoicePaymentRefundEvent);
            log.info("Invoice payment refund event status have been changed, refundId='{}', paymentId='{}', invoiceId='{}', eventId='{}', refundStatus='{}'",
                    invoicePaymentRefundEvent.getPaymentRefundId(), invoicePaymentRefundEvent.getPaymentId(), invoicePaymentRefundEvent.getInvoiceId(), invoicePaymentRefundEvent.getEventId(), invoicePaymentRefundEvent.getPaymentRefundStatus());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice payment refund event status, refundId='%s', paymentId='%s', invoiceId='%s', eventId='%d', refundStatus='%s'",
                    refundStatusEvent.getPaymentRefundId(), refundStatusEvent.getPaymentId(), refundStatusEvent.getInvoiceId(), refundStatusEvent.getEventId(), refundStatusEvent.getPaymentRefundStatus());
            throw new StorageException(message, ex);
        }
    }

}

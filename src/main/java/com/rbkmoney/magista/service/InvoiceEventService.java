package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class InvoiceEventService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceEventDao invoiceEventDao;

    public InvoiceEventStat getInvoiceEventById(String invoiceId) {
        return invoiceEventDao.findInvoiceById(invoiceId);
    }

    public InvoiceEventStat getInvoicePaymentEventByIds(String invoiceId, String paymentId) {
        return invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceId, paymentId);
    }

    public void changeInvoiceEventStatus(InvoiceEventStat invoiceStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice event status, invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoiceStatusEvent.getInvoiceId(), invoiceStatusEvent.getEventId(), invoiceStatusEvent.getInvoiceStatus());

        try {
            InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoiceStatusEvent.getInvoiceId());
            if (invoiceEvent == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'",
                        invoiceStatusEvent.getInvoiceId(), invoiceStatusEvent.getEventId()));
            }

            invoiceEvent.setEventType(InvoiceEventType.INVOICE_STATUS_CHANGED);
            invoiceEvent.setEventId(invoiceStatusEvent.getEventId());
            invoiceEvent.setEventCreatedAt(invoiceStatusEvent.getEventCreatedAt());

            invoiceEvent.setInvoiceStatus(invoiceStatusEvent.getInvoiceStatus());
            invoiceEvent.setInvoiceStatusDetails(invoiceStatusEvent.getInvoiceStatusDetails());

            invoiceEventDao.update(invoiceEvent);
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
            InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getPaymentId());
            if (invoicePaymentEvent == null) {
                throw new NotFoundException(String.format("Invoice payment event not found, paymentId='%s', invoiceId='%s', eventId='%d'",
                        invoiceAdjustmentEvent.getPaymentId(), invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getEventId()));
            }

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);
            invoicePaymentEvent.setEventId(invoiceAdjustmentEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoiceAdjustmentEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentAdjustmentId(invoiceAdjustmentEvent.getPaymentAdjustmentId());
            invoicePaymentEvent.setPaymentAdjustmentReason(invoiceAdjustmentEvent.getPaymentAdjustmentReason());
            invoicePaymentEvent.setPaymentAdjustmentStatus(invoiceAdjustmentEvent.getPaymentAdjustmentStatus());
            invoicePaymentEvent.setPaymentAdjustmentStatusCreatedAt(invoiceAdjustmentEvent.getPaymentAdjustmentStatusCreatedAt());
            invoicePaymentEvent.setPaymentAdjustmentCreatedAt(invoiceAdjustmentEvent.getPaymentAdjustmentCreatedAt());
            invoicePaymentEvent.setPaymentAdjustmentFee(invoiceAdjustmentEvent.getPaymentAdjustmentFee());
            invoicePaymentEvent.setPaymentAdjustmentProviderFee(invoiceAdjustmentEvent.getPaymentAdjustmentProviderFee());
            invoicePaymentEvent.setPaymentAdjustmentExternalFee(invoiceAdjustmentEvent.getPaymentAdjustmentExternalFee());

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Adjustment event have been saved, event='{}'", invoicePaymentEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save adjustment event, paymentId='%s', invoiceId='%s', eventId='%d'",
                    invoiceAdjustmentEvent.getPaymentId(), invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getEventId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentAdjustmentStatus(InvoiceEventStat invoiceAdjustmentStatusEvent) {
        log.debug("Change adjustment event status, adjustmentId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                invoiceAdjustmentStatusEvent.getPaymentAdjustmentId(), invoiceAdjustmentStatusEvent.getPaymentId(), invoiceAdjustmentStatusEvent.getInvoiceId(), invoiceAdjustmentStatusEvent.getEventId());

        try {
            InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceAdjustmentStatusEvent.getInvoiceId(), invoiceAdjustmentStatusEvent.getPaymentId());
            if (invoicePaymentEvent == null) {
                throw new NotFoundException(String.format("Invoice payment event not found, paymentId='%s', invoiceId='%s', eventId='%d'",
                        invoiceAdjustmentStatusEvent.getPaymentId(), invoiceAdjustmentStatusEvent.getInvoiceId(), invoiceAdjustmentStatusEvent.getEventId()));
            }

            if (!invoicePaymentEvent.getPaymentAdjustmentId().equals(invoiceAdjustmentStatusEvent.getPaymentAdjustmentId())) {
                throw new NotFoundException(
                        String.format("Adjustment not found, adjustmentId='%s', invoiceId='%s', paymentId='%s', eventId='%d'",
                                invoiceAdjustmentStatusEvent.getPaymentAdjustmentId(),
                                invoiceAdjustmentStatusEvent.getInvoiceId(),
                                invoiceAdjustmentStatusEvent.getPaymentId(),
                                invoiceAdjustmentStatusEvent.getEventId())
                );
            }

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED);
            invoicePaymentEvent.setEventId(invoiceAdjustmentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoiceAdjustmentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentAdjustmentStatus(invoiceAdjustmentStatusEvent.getPaymentAdjustmentStatus());
            invoicePaymentEvent.setPaymentAdjustmentStatusCreatedAt(invoiceAdjustmentStatusEvent.getPaymentAdjustmentStatusCreatedAt());

            if (invoicePaymentEvent.getPaymentAdjustmentStatus() == AdjustmentStatus.captured) {
                invoicePaymentEvent.setPaymentFee(invoicePaymentEvent.getPaymentAdjustmentFee());
                invoicePaymentEvent.setPaymentProviderFee(invoicePaymentEvent.getPaymentAdjustmentProviderFee());
                invoicePaymentEvent.setPaymentExternalFee(invoicePaymentEvent.getPaymentAdjustmentExternalFee());
            }

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Adjustment event status have been changed, invoiceId='{}', eventId='{}', AdjustmentStatus='{}'",
                    invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId(), invoicePaymentEvent.getPaymentAdjustmentStatus());
        } catch (DaoException ex) {
            String message = String.format("Failed to change adjustment event status, paymentId='%s', invoiceId='%s', eventId='%d'",
                    invoiceAdjustmentStatusEvent.getPaymentId(), invoiceAdjustmentStatusEvent.getInvoiceId(), invoiceAdjustmentStatusEvent.getEventId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentStatus(InvoiceEventStat invoicePaymentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId(), invoicePaymentStatusEvent.getPaymentStatus());

        try {
            InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getPaymentId());
            if (invoicePaymentEvent == null) {
                throw new NotFoundException(String.format("Invoice payment event not found, paymentId='%s', invoiceId='%s', eventId='%d'",
                        invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId()));
            }

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED);
            invoicePaymentEvent.setEventId(invoicePaymentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoicePaymentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentStatus(invoicePaymentStatusEvent.getPaymentStatus());
            invoicePaymentEvent.setPaymentStatusFailureCode(invoicePaymentStatusEvent.getPaymentStatusFailureCode());
            invoicePaymentEvent.setPaymentStatusFailureDescription(invoicePaymentStatusEvent.getPaymentStatusFailureDescription());

            invoiceEventDao.update(invoicePaymentEvent);
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
            InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoicePaymentEvent.getInvoiceId());
            if (invoiceEvent == null) {
                throw new NotFoundException(String.format("Invoice event not found, invoiceId='%s', eventId='%d'", invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId()));
            }

            invoicePaymentEvent.setInvoiceProduct(invoiceEvent.getInvoiceProduct());
            invoicePaymentEvent.setInvoiceDescription(invoiceEvent.getInvoiceDescription());
            invoicePaymentEvent.setInvoiceCurrencyCode(invoiceEvent.getInvoiceCurrencyCode());
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

}

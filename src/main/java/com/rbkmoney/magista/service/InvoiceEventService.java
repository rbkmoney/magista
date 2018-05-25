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

    public InvoiceEventStat getInvoiceEventById(String invoiceId) throws NotFoundException {
        InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoiceId);
        if (invoiceEvent == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", invoiceId));
        }
        return invoiceEvent;
    }

    public InvoiceEventStat getInvoicePaymentEventByIds(String invoiceId, String paymentId) throws NotFoundException {
        InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceId, paymentId);
        if (invoicePaymentEvent == null) {
            throw new NotFoundException(String.format("Invoice payment not found, invoiceId='%s', paymentId='%s'", invoiceId, paymentId));
        }
        return invoicePaymentEvent;
    }

    public void changeInvoiceEventStatus(InvoiceEventStat invoiceStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice event status, invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoiceStatusEvent.getInvoiceId(), invoiceStatusEvent.getEventId(), invoiceStatusEvent.getInvoiceStatus());

        try {
            InvoiceEventStat invoiceEvent = getInvoiceEventById(invoiceStatusEvent.getInvoiceId());

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
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoiceAdjustmentEvent.getInvoiceId(),
                    invoiceAdjustmentEvent.getPaymentId()
            );

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);
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

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Adjustment event have been saved, event='{}'", invoicePaymentEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save adjustment event, paymentId='%s', invoiceId='%s', eventId='%d'",
                    invoiceAdjustmentEvent.getPaymentId(), invoiceAdjustmentEvent.getInvoiceId(), invoiceAdjustmentEvent.getEventId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentAdjustmentStatus(InvoiceEventStat invoiceAdjustmentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change adjustment event status, adjustmentId='{}', paymentId='{}', invoiceId='{}', eventId='{}'",
                invoiceAdjustmentStatusEvent.getPaymentAdjustmentId(), invoiceAdjustmentStatusEvent.getPaymentId(), invoiceAdjustmentStatusEvent.getInvoiceId(), invoiceAdjustmentStatusEvent.getEventId());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoiceAdjustmentStatusEvent.getInvoiceId(),
                    invoiceAdjustmentStatusEvent.getPaymentId()
            );

            if (!invoicePaymentEvent.getPaymentAdjustmentId().equals(invoiceAdjustmentStatusEvent.getPaymentAdjustmentId())) {
                throw new NotFoundException(
                        String.format("Adjustment not found, adjustmentId='%s', invoiceId='%s', paymentId='%s', eventId='%d'",
                                invoiceAdjustmentStatusEvent.getPaymentAdjustmentId(),
                                invoiceAdjustmentStatusEvent.getInvoiceId(),
                                invoiceAdjustmentStatusEvent.getPaymentId(),
                                invoiceAdjustmentStatusEvent.getEventId())
                );
            }

            if (invoicePaymentEvent.getPaymentAdjustmentStatus() != AdjustmentStatus.pending) {
                throw new AdjustmentException(
                        String.format("Illegal adjustment status, adjustmentId='%s', invoiceId='%s', paymentId='%s', eventId='%d', adjustmentStatus='%s'",
                                invoiceAdjustmentStatusEvent.getPaymentAdjustmentId(),
                                invoiceAdjustmentStatusEvent.getInvoiceId(),
                                invoiceAdjustmentStatusEvent.getPaymentId(),
                                invoiceAdjustmentStatusEvent.getEventId(),
                                invoiceAdjustmentStatusEvent.getPaymentAdjustmentStatus())
                );
            }

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED);
            invoicePaymentEvent.setEventId(invoiceAdjustmentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoiceAdjustmentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentAdjustmentStatus(invoiceAdjustmentStatusEvent.getPaymentAdjustmentStatus());
            invoicePaymentEvent.setPaymentAdjustmentStatusCreatedAt(invoiceAdjustmentStatusEvent.getPaymentAdjustmentStatusCreatedAt());

            if (invoicePaymentEvent.getPaymentAdjustmentStatus() == AdjustmentStatus.captured) {
                invoicePaymentEvent.setPaymentAmount(invoicePaymentEvent.getPaymentAdjustmentAmount());
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

    public void handleTerminalRecieptEvent(InvoiceEventStat paymentTerminalRecieptEvent) throws NotFoundException, StorageException {
        log.debug("Handle payment terminal reciept event, paymentId='{}', invoiceId='{}', eventId='{}', paymentShortId='{}'",
                paymentTerminalRecieptEvent.getPaymentId(), paymentTerminalRecieptEvent.getInvoiceId(), paymentTerminalRecieptEvent.getEventId(), paymentTerminalRecieptEvent.getPaymentShortId());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    paymentTerminalRecieptEvent.getInvoiceId(),
                    paymentTerminalRecieptEvent.getPaymentId()
            );

            invoicePaymentEvent.setEventType(InvoiceEventType.PAYMENT_TERMINAL_RECIEPT);
            invoicePaymentEvent.setEventId(paymentTerminalRecieptEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(paymentTerminalRecieptEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentShortId(paymentTerminalRecieptEvent.getPaymentShortId());

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Payment terminal reciept event have been handled, paymentId='{}', invoiceId='{}', eventId='{}', paymentShortId='{}'",
                    invoicePaymentEvent.getPaymentId(), invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId(), invoicePaymentEvent.getPaymentShortId());

        } catch (DaoException ex) {
            String message = String.format("Failed to handle payment terminal reciept event, paymentId='%s', invoiceId='%s', eventId='%d', paymentShortId='%s'",
                    paymentTerminalRecieptEvent.getPaymentId(), paymentTerminalRecieptEvent.getInvoiceId(), paymentTerminalRecieptEvent.getEventId(), paymentTerminalRecieptEvent.getPaymentShortId());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentStatus(InvoiceEventStat invoicePaymentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId(), invoicePaymentStatusEvent.getPaymentStatus());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoicePaymentStatusEvent.getInvoiceId(),
                    invoicePaymentStatusEvent.getPaymentId()
            );

            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED);
            invoicePaymentEvent.setEventId(invoicePaymentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoicePaymentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentStatus(invoicePaymentStatusEvent.getPaymentStatus());

            invoicePaymentEvent.setPaymentFailureClass(invoicePaymentStatusEvent.getPaymentFailureClass());
            invoicePaymentEvent.setPaymentExternalFailureCode(invoicePaymentStatusEvent.getPaymentExternalFailureCode());
            invoicePaymentEvent.setPaymentExternalFailureDescription(invoicePaymentStatusEvent.getPaymentExternalFailureDescription());

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
            InvoiceEventStat invoiceEvent = getInvoiceEventById(invoicePaymentEvent.getInvoiceId());

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
            invoicePaymentEvent.setPaymentInstitutionId(invoiceEvent.getPaymentInstitutionId());
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
                    refundEventStat.getPaymentId()
            );

            invoicePaymentEvent.setPaymentRefundId(refundEventStat.getPaymentRefundId());
            invoicePaymentEvent.setPaymentRefundReason(refundEventStat.getPaymentRefundReason());
            invoicePaymentEvent.setPaymentRefundStatus(refundEventStat.getPaymentRefundStatus());
            invoicePaymentEvent.setPaymentRefundCreatedAt(refundEventStat.getPaymentRefundCreatedAt());
            invoicePaymentEvent.setPaymentRefundFee(refundEventStat.getPaymentRefundFee());
            invoicePaymentEvent.setPaymentRefundExternalFee(refundEventStat.getPaymentRefundExternalFee());
            invoicePaymentEvent.setPaymentRefundProviderFee(refundEventStat.getPaymentRefundProviderFee());

            invoiceEventDao.update(invoicePaymentEvent);
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
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    refundStatusEvent.getInvoiceId(),
                    refundStatusEvent.getPaymentId()
            );

            if (!refundStatusEvent.getPaymentRefundId().equals(invoicePaymentEvent.getPaymentRefundId())) {
                throw new NotFoundException(
                        String.format("Refund not found, refundId='%s', invoiceId='%s', paymentId='%s', eventId='%d'",
                                refundStatusEvent.getPaymentRefundId(),
                                refundStatusEvent.getInvoiceId(),
                                refundStatusEvent.getPaymentId(),
                                refundStatusEvent.getEventId())
                );
            }

            invoicePaymentEvent.setPaymentRefundStatus(refundStatusEvent.getPaymentRefundStatus());

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Invoice payment refund event status have been changed, refundId='{}', paymentId='{}', invoiceId='{}', eventId='{}', refundStatus='{}'",
                    invoicePaymentEvent.getPaymentRefundId(), invoicePaymentEvent.getPaymentId(), invoicePaymentEvent.getInvoiceId(), invoicePaymentEvent.getEventId(), invoicePaymentEvent.getPaymentRefundStatus());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice payment refund event status, refundId='%s', paymentId='%s', invoiceId='%s', eventId='%d', refundStatus='%s'",
                    refundStatusEvent.getPaymentRefundId(), refundStatusEvent.getPaymentId(), refundStatusEvent.getInvoiceId(), refundStatusEvent.getEventId(), refundStatusEvent.getPaymentRefundStatus());
            throw new StorageException(message, ex);
        }
    }

}

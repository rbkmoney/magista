package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.domain.OperationFailure;
import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.magista.model.PaymentStatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    public void changeInvoiceEventStatus(InvoiceStatusChange invoiceStatusChange) throws NotFoundException, StorageException {
        log.debug("Change invoice event status, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());

        try {
            InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoiceStatusChange.getInvoiceId());
            if (invoiceEvent == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId()));
            }

            invoiceEvent.setEventId(invoiceStatusChange.getEventId());
            invoiceEvent.setEventType(InvoiceEventType.INVOICE_STATUS_CHANGED);

            InvoiceStatus status = invoiceStatusChange.getStatus();
            invoiceEvent.setInvoiceStatus(
                    com.rbkmoney.magista.domain.enums.InvoiceStatus.valueOf(status.getSetField().getFieldName())
            );
            if (status.isSetCancelled()) {
                invoiceEvent.setInvoiceStatusDetails(status.getCancelled().getDetails());
            } else if (status.isSetFulfilled()) {
                invoiceEvent.setInvoiceStatusDetails(status.getFulfilled().getDetails());
            }
            invoiceEvent.setEventCreatedAt(LocalDateTime.ofInstant(invoiceStatusChange.getChangedAt(), ZoneOffset.UTC));

            invoiceEventDao.update(invoiceEvent);
            log.info("Invoice event status have been changed, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice event status, invoiceId='%s', eventId='%d', invoiceStatus='%s'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void changeInvoicePaymentStatus(PaymentStatusChange paymentStatusChange) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());

        try {
            InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByInvoiceAndPaymentId(paymentStatusChange.getInvoiceId(), paymentStatusChange.getPaymentId());
            if (invoicePaymentEvent == null) {
                throw new NotFoundException(String.format("Invoice payment event not found, paymentId='%s', invoiceId='%s', eventId='%d'",
                        paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId()));
            }

            invoicePaymentEvent.setEventId(paymentStatusChange.getEventId());
            invoicePaymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED);

            InvoicePaymentStatus status = paymentStatusChange.getStatus();
            invoicePaymentEvent.setPaymentStatus(
                    com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.valueOf(status.getSetField().getFieldName())
            );
            if (status.isSetFailed()) {
                OperationFailure operationFailure = status.getFailed().getFailure();
                invoicePaymentEvent.setPaymentStatusFailureCode(operationFailure.getCode());
                invoicePaymentEvent.setPaymentStatusFailureDescription(operationFailure.getDescription());
            }
            invoicePaymentEvent.setEventCreatedAt(LocalDateTime.ofInstant(paymentStatusChange.getChangedAt(), ZoneOffset.UTC));

            invoiceEventDao.update(invoicePaymentEvent);
            log.info("Invoice payment event status have been changed, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                    paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice payment event status, paymentId='%s', invoiceId='%s', eventId='%d', invoiceStatus='%s'",
                    paymentStatusChange.getPaymentId(), paymentStatusChange.getInvoiceId(), paymentStatusChange.getEventId(), paymentStatusChange.getStatus().getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoiceEvent(InvoiceEventStat invoiceEvent) throws StorageException {
        log.debug("Save invoice event, event='{}'", invoiceEvent);

        try {
            invoiceEventDao.insert(invoiceEvent);
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

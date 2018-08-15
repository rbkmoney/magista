package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
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

    public InvoiceEventStat getInvoiceEventById(String invoiceId, InvoiceEventType eventType) throws NotFoundException {
        InvoiceEventStat invoiceEvent = invoiceEventDao.findInvoiceById(invoiceId);
        if (invoiceEvent == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventType='%s'", invoiceId, eventType));
        }
        return invoiceEvent;
    }

    public InvoiceEventStat getInvoicePaymentEventByIds(String invoiceId, String paymentId) throws NotFoundException {
        InvoiceEventStat invoicePaymentEvent = invoiceEventDao.findPaymentByIds(invoiceId, paymentId);
        if (invoicePaymentEvent == null) {
            throw new NotFoundException(String.format("Invoice payment not found, invoiceId='%s', paymentId='%s'", invoiceId, paymentId));
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

    public void changeInvoicePaymentStatus(InvoiceEventStat invoicePaymentStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change invoice payment event status, paymentId='{}', invoiceId='{}', eventId='{}', invoiceStatus='{}'",
                invoicePaymentStatusEvent.getPaymentId(), invoicePaymentStatusEvent.getInvoiceId(), invoicePaymentStatusEvent.getEventId(), invoicePaymentStatusEvent.getPaymentStatus());

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoicePaymentStatusEvent.getInvoiceId(),
                    invoicePaymentStatusEvent.getPaymentId()
            );

            invoicePaymentEvent.setId(invoicePaymentStatusEvent.getId());
            invoicePaymentEvent.setEventType(invoicePaymentStatusEvent.getEventType());
            invoicePaymentEvent.setEventId(invoicePaymentStatusEvent.getEventId());
            invoicePaymentEvent.setEventCreatedAt(invoicePaymentStatusEvent.getEventCreatedAt());

            invoicePaymentEvent.setPaymentStatus(invoicePaymentStatusEvent.getPaymentStatus());

            invoicePaymentEvent.setPaymentOperationFailureClass(invoicePaymentStatusEvent.getPaymentOperationFailureClass());
            invoicePaymentEvent.setPaymentExternalFailure(invoicePaymentStatusEvent.getPaymentExternalFailure());
            invoicePaymentEvent.setPaymentExternalFailureReason(invoicePaymentStatusEvent.getPaymentExternalFailureReason());

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
            invoicePaymentEvent.setPaymentInstitutionId(invoiceEvent.getPaymentInstitutionId());

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

    public void saveInvoicePaymentCashFlowEvent(InvoiceEventStat invoicePaymentCashFlowEvent) {
        log.debug("Save invoice payment cash flow event, event='{}'", invoicePaymentCashFlowEvent);

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoicePaymentCashFlowEvent.getInvoiceId(),
                    invoicePaymentCashFlowEvent.getPaymentId()
            );
            invoicePaymentCashFlowEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_CASH_FLOW_CHANGED);
            BeanUtil.merge(invoicePaymentEvent, invoicePaymentCashFlowEvent, "id");
            invoiceEventDao.insert(invoicePaymentCashFlowEvent);
            log.info("Invoice payment cash flow event have been saved, event='{}'", invoicePaymentCashFlowEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice payment cash flow event, event='%s'", invoicePaymentCashFlowEvent);
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoicePaymentRouteEvent(InvoiceEventStat invoicePaymentRouteEventStat) {
        log.debug("Save invoice payment route event, event='{}'", invoicePaymentRouteEventStat);

        try {
            InvoiceEventStat invoicePaymentEvent = getInvoicePaymentEventByIds(
                    invoicePaymentRouteEventStat.getInvoiceId(),
                    invoicePaymentRouteEventStat.getPaymentId()
            );
            invoicePaymentRouteEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_ROUTE_CHANGED);
            BeanUtil.merge(invoicePaymentEvent, invoicePaymentRouteEventStat, "id");
            invoiceEventDao.insert(invoicePaymentRouteEventStat);
            log.info("Invoice payment route event have been saved, event='{}'", invoicePaymentRouteEventStat);
        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice payment route event, event='%s'", invoicePaymentRouteEventStat);
            throw new StorageException(message, ex);
        }
    }
}

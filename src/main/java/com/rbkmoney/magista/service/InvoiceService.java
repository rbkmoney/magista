package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Created by tolkonepiu on 22.08.16.
 */
@Service
public class InvoiceService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceDao invoiceDao;

    public Invoice getInvoiceById(String invoiceId) throws StorageException {
        return invoiceDao.findById(invoiceId);
    }

    public void changeInvoiceStatus(String invoiceId, long eventId, InvoiceStatus status, Instant changedAt) throws NotFoundException, StorageException {
        log.trace("Change invoice status, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceId, eventId, status.getSetField().getFieldName());

        try {
            Invoice invoice = invoiceDao.findById(invoiceId);
            if (invoice == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'", invoiceId, eventId));
            }

            invoice.setStatus(status.getSetField());
            invoice.setChangedAt(changedAt);
            invoice.getModel().setStatus(status);

            invoiceDao.update(invoice);
            log.info("Invoice status have been changed, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceId, eventId, status.getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice status, invoiceId='%s', eventId='%d', invoiceStatus='%s'", invoiceId, eventId, status.getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoice(long eventId, InvoiceCreated invoiceCreated) throws StorageException {
        log.trace("Save invoice, invoiceId='{}', eventId='{}'", invoiceCreated.getInvoice().getId(), eventId);

        try {
            Invoice invoice = new Invoice();
            invoice.setId(invoiceCreated.getInvoice().getId());
            invoice.setEventId(eventId);
            invoice.setShopId(invoiceCreated.getInvoice().getShopId());
            invoice.setMerchantId(invoiceCreated.getInvoice().getOwner().getId());
            invoice.setStatus(invoiceCreated.getInvoice().getStatus().getSetField());

            Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt()));
            invoice.setCreatedAt(createdAt);
            invoice.setChangedAt(createdAt);

            invoice.setAmount(invoiceCreated.getInvoice().getCost().getAmount());
            invoice.setCurrencyCode(invoiceCreated.getInvoice().getCost().getCurrency().getSymbolicCode());
            invoice.setModel(invoiceCreated.getInvoice());

            invoiceDao.insert(invoice);
            log.info("Invoice have been saved, invoiceId='{}', eventId='{}'", invoiceCreated.getInvoice().getId(), eventId);

        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice, id='%s', eventId='%d'", invoiceCreated.getInvoice().getId(), eventId);
            throw new StorageException(message, ex);
        }
    }

}

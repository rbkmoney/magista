package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tolkonepiu on 22.08.16.
 */
@Service
public class InvoiceService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceDao invoiceDao;

    public Invoice getInvoiceById(String invoiceId) throws StorageException {
        return invoiceDao.findById(invoiceId);
    }

    public void changeInvoiceStatus(InvoiceStatusChange invoiceStatusChange) throws NotFoundException, StorageException {
        log.trace("Change invoice status, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());

        try {
            Invoice invoice = invoiceDao.findById(invoiceStatusChange.getInvoiceId());
            if (invoice == null) {
                throw new NotFoundException(String.format("Invoice not found, invoiceId='%s', eventId='%d'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId()));
            }

            InvoiceStatus status = invoiceStatusChange.getStatus();
            invoice.setStatus(invoiceStatusChange.getStatus().getSetField());
            invoice.setStatus(status.getSetField());
            if (status.isSetCancelled()) {
                invoice.setStatusDetails(status.getCancelled().getDetails());
            } else if (status.isSetFulfilled()) {
                invoice.setStatusDetails(status.getFulfilled().getDetails());
            }
            invoice.setChangedAt(invoiceStatusChange.getChangedAt());

            invoiceDao.update(invoice);
            log.info("Invoice status have been changed, invoiceId='{}', eventId='{}', invoiceStatus='{}'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());

        } catch (DaoException ex) {
            String message = String.format("Failed to change invoice status, invoiceId='%s', eventId='%d', invoiceStatus='%s'", invoiceStatusChange.getInvoiceId(), invoiceStatusChange.getEventId(), invoiceStatusChange.getStatus().getSetField().getFieldName());
            throw new StorageException(message, ex);
        }
    }

    public void saveInvoice(Invoice invoice) throws StorageException {
        log.trace("Save invoice, invoiceId='{}', eventId='{}'", invoice.getId(), invoice.getEventId());
        try {
            invoiceDao.insert(invoice);
            log.info("Invoice have been saved, invoiceId='{}', eventId='{}'", invoice.getId(), invoice.getEventId());

        } catch (DaoException ex) {
            String message = String.format("Failed to save invoice, id='%s', eventId='%d'", invoice.getId(), invoice.getEventId());
            throw new StorageException(message, ex);
        }
    }

}

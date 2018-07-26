package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
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
public class InvoiceService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveInvoice(InvoiceData invoiceData, InvoiceEvent invoiceEvent) throws NotFoundException, StorageException {
        log.info("Trying to save invoice, invoiceData='{}', invoiceEvent='{}'", invoiceData, invoiceEvent);
        try {
            invoiceDao.saveInvoiceData(invoiceData);
            invoiceDao.saveInvoiceEvent(invoiceEvent);
            log.info("Invoice have been saved, invoiceData='{}', invoiceEvent='{}'", invoiceData, invoiceEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save invoice, invoiceData='%s', invoiceEvent='%s'", invoiceData, invoiceEvent), ex);
        }
    }

    public void saveInvoiceChange(InvoiceEvent invoiceEvent) throws StorageException {
        log.info("Trying to save invoice change, invoiceEvent='{}'", invoiceEvent);
        try {
            invoiceDao.saveInvoiceEvent(invoiceEvent);
            log.info("Invoice change have been saved, invoiceEvent='{}'", invoiceEvent);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save invoice change, invoiceEvent='%s'", invoiceEvent), ex);
        }
    }

}

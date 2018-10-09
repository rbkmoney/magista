package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    private final Cache<String, InvoiceData> invoiceDataCache;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao, @Value("${cache.invoiceData.size}") int cacheSize) {
        this.invoiceDao = invoiceDao;
        this.invoiceDataCache = Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    public InvoiceData getInvoiceData(String invoiceId) throws NotFoundException, StorageException {
        return invoiceDataCache.get(
                invoiceId,
                key -> {
                    try {
                        InvoiceData invoiceData = invoiceDao.getInvoiceData(key);
                        if (invoiceData == null) {
                            throw new NotFoundException(String.format("Invoice data not found, invoiceId='%s'", key));
                        }
                        return invoiceData;
                    } catch (DaoException ex) {
                        throw new StorageException(String.format("Failed to get invoice data, invoiceId='%s'", key), ex);
                    }
                }
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveInvoice(InvoiceData invoiceData, InvoiceEvent invoiceEvent) throws NotFoundException, StorageException {
        log.info("Trying to save invoice, invoiceData='{}', invoiceEvent='{}'", invoiceData, invoiceEvent);
        try {
            invoiceDao.saveInvoiceData(invoiceData);
            invoiceDataCache.put(invoiceData.getInvoiceId(), invoiceData);
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

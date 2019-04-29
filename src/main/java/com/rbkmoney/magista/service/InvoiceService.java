package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
                        InvoiceData invoiceData = invoiceDao.get(key);
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

    public void saveInvoice(InvoiceData invoiceData) throws NotFoundException, StorageException {
        log.info("Trying to save invoice, invoiceData='{}'", invoiceData);
        try {
            invoiceDao.save(invoiceData);
            invoiceDataCache.put(invoiceData.getInvoiceId(), invoiceData);
            log.info("Invoice have been saved, invoiceData='{}'", invoiceData);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save invoice, invoiceData='%s'", invoiceData), ex);
        }
    }

}

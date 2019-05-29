package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.domain.enums.InvoiceEventType.INVOICE_STATUS_CHANGED;

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

    public void saveInvoices(List<InvoiceData> invoiceEvents) throws NotFoundException, StorageException {
        log.info("Trying to save invoice events, size={}", invoiceEvents.size());

        List<InvoiceData> enrichedInvoiceEvents = invoiceEvents.stream()
                .map(invoiceData -> {
                    if (invoiceData.getEventType() == INVOICE_STATUS_CHANGED) {
                        InvoiceData previousInvoiceData = invoiceDao.get(invoiceData.getInvoiceId());
                        BeanUtil.merge(previousInvoiceData, invoiceData);
                    }
                    return invoiceData;
                })
                .peek(invoiceData -> invoiceDataCache.put(invoiceData.getInvoiceId(), invoiceData))
                .collect(Collectors.toList());

        try {
            invoiceDao.save(enrichedInvoiceEvents);
            log.info("Invoice have been saved, size={}", enrichedInvoiceEvents.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save invoice, size=%d", enrichedInvoiceEvents.size()), ex);
        }
    }

}

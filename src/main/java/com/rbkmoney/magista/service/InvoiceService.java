package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import com.rbkmoney.magista.util.StreamUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.domain.enums.InvoiceEventType.INVOICE_STATUS_CHANGED;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceDao invoiceDao;

    private final Cache<String, InvoiceData> invoiceDataCache;

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
        List<InvoiceData> enrichedInvoiceEvents = invoiceEvents.stream()
                .map(invoiceData -> {
                    if (invoiceData.getEventType() == INVOICE_STATUS_CHANGED) {
                        InvoiceData previousInvoiceData = getInvoiceData(invoiceData.getInvoiceId());
                        BeanUtil.merge(previousInvoiceData, invoiceData);
                    }
                    return invoiceData;
                })
                .peek(invoiceData -> invoiceDataCache.put(invoiceData.getInvoiceId(), invoiceData))
                .collect(Collectors.toList());

        List<InvoiceData> invoiceCreatedEvents = enrichedInvoiceEvents.stream()
                .filter(invoiceData -> invoiceData.getEventType() == InvoiceEventType.INVOICE_CREATED)
                .collect(Collectors.toList());
        enrichedInvoiceEvents.removeAll(invoiceCreatedEvents);
        List<InvoiceData> updatedInvoices = StreamUtil.groupAndReduce(
                enrichedInvoiceEvents,
                invoiceData -> invoiceData.getInvoiceId(),
                (o1, o2) -> o2
        );

        try {
            invoiceDao.insert(invoiceCreatedEvents);
            invoiceDao.update(updatedInvoices);
            log.info("Payment event have been saved, batchSize={}, insertsCount={}, updatesCount={}", invoiceEvents.size(), invoiceCreatedEvents.size(), updatedInvoices.size());
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save invoice events, batchSize=%d, insertsCount=%d, updatesCount=%d", invoiceEvents.size(), invoiceCreatedEvents.size(), updatedInvoices.size()), ex);
        }
    }

}

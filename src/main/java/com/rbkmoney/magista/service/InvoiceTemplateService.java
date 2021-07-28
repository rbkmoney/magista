package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceTemplateDao;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.InvoiceTemplateAlreadyDeletedException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceTemplateService {

    private final InvoiceTemplateDao invoiceTemplateDao;

    public InvoiceTemplate get(String invoiceTemplateId) {
        try {
            InvoiceTemplate invoiceTemplate = invoiceTemplateDao.get(invoiceTemplateId);
            if (invoiceTemplate == null) {
                throw new NotFoundException(
                        String.format("InvoiceTemplate not found, invoiceTemplateId='%s'",
                                invoiceTemplateId));
            }
            if (invoiceTemplate.getEventType() == InvoiceTemplateEventType.INVOICE_TEMPLATE_DELETED) {
                throw new InvoiceTemplateAlreadyDeletedException(
                        String.format(
                                "'Get' operation for InvoiceTemplate in status INVOICE_TEMPLATE_DELETED, " +
                                        "invoiceTemplateId='%s'",
                                invoiceTemplateId));
            }
            return invoiceTemplate;
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to get InvoiceTemplate, invoiceTemplateId='%s'",
                            invoiceTemplateId),
                    ex);
        }
    }

    public void save(List<InvoiceTemplate> invoiceTemplates) {
        log.info("Trying to save InvoiceTemplate events, size={}", invoiceTemplates.size());
        Map<String, InvoiceTemplate> invoiceTemplatesMap = new HashMap<>();
        List<InvoiceTemplate> enriched = invoiceTemplates.stream()
                .peek(invoiceTemplate -> {
                    String invoiceTemplateId = invoiceTemplate.getInvoiceTemplateId();
                    if (invoiceTemplate.getEventType() != InvoiceTemplateEventType.INVOICE_TEMPLATE_CREATED) {
                        InvoiceTemplate previousInvoiceTemplate = invoiceTemplatesMap.computeIfAbsent(
                                invoiceTemplateId,
                                key -> get(invoiceTemplateId));
                        BeanUtil.merge(previousInvoiceTemplate, invoiceTemplate);
                    }
                })
                .peek(invoiceTemplate -> invoiceTemplatesMap.put(
                        invoiceTemplate.getInvoiceTemplateId(),
                        invoiceTemplate))
                .collect(Collectors.toList());

        try {
            invoiceTemplateDao.save(enriched);
            log.info("InvoiceTemplate events have been saved, size={}", enriched.size());
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to save chargeback events, size=%d", enriched.size()), ex);
        }
    }
}

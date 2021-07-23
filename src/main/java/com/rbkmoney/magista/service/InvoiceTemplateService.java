package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.InvoiceTemplateDao;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.exception.*;
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
        InvoiceTemplate invoiceTemplate = getInvoiceTemplate(invoiceTemplateId);
        if (invoiceTemplate == null) {
            throw getNotFoundException(invoiceTemplateId);
        }
        if (invoiceTemplate.getEventType() == InvoiceTemplateEventType.INVOICE_TEMPLATE_DELETED) {
            throw getInvoiceTemplateAlreadyDeletedException(invoiceTemplateId);
        }
        return invoiceTemplate;
    }

    public void save(List<InvoiceTemplate> invoiceTemplates) {
        log.info("Trying to save InvoiceTemplate events, size={}", invoiceTemplates.size());
        Map<String, InvoiceTemplate> invoiceTemplatesMap = new HashMap<>();
        List<InvoiceTemplate> enriched = invoiceTemplates.stream()
                .map(current -> {
                    String invoiceTemplateId = current.getInvoiceTemplateId();
                    InvoiceTemplate previous = invoiceTemplatesMap.computeIfAbsent(
                            invoiceTemplateId,
                            key -> getInvoiceTemplate(invoiceTemplateId));
                    if (previous == null) {
                        if (current.getEventType() == InvoiceTemplateEventType.INVOICE_TEMPLATE_CREATED) {
                            return current;
                        } else {
                            throw getNotFoundException(invoiceTemplateId);
                        }
                    } else {
                        if (current.getEventType() == InvoiceTemplateEventType.INVOICE_TEMPLATE_CREATED) {
                            throw new InvoiceTemplateAlreadyCreatedException(
                                    String.format(
                                            "InvoiceTemplate in status INVOICE_TEMPLATE_CREATED, " +
                                                    "but InvoiceTemplate is exist with" +
                                                    "invoiceTemplateId='%s'",
                                            invoiceTemplateId));
                        } else if (previous.getEventType() == InvoiceTemplateEventType.INVOICE_TEMPLATE_DELETED) {
                            throw getInvoiceTemplateAlreadyDeletedException(invoiceTemplateId);
                        } else {
                            BeanUtil.merge(previous, current);
                            return current;
                        }
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

    private InvoiceTemplate getInvoiceTemplate(String invoiceTemplateId) {
        try {
            return invoiceTemplateDao.get(invoiceTemplateId);
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to get InvoiceTemplate, invoiceTemplateId='%s'",
                            invoiceTemplateId),
                    ex);
        }
    }

    private NotFoundException getNotFoundException(String invoiceTemplateId) {
        return new NotFoundException(
                String.format("InvoiceTemplate not found, invoiceTemplateId='%s'",
                        invoiceTemplateId));
    }

    private InvoiceTemplateAlreadyDeletedException getInvoiceTemplateAlreadyDeletedException(
            String invoiceTemplateId) {
        return new InvoiceTemplateAlreadyDeletedException(
                String.format(
                        "'Get' operation for InvoiceTemplate in status INVOICE_TEMPLATE_DELETED, " +
                                "invoiceTemplateId='%s'",
                        invoiceTemplateId));
    }
}

package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.config.AbstractDaoConfig;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandom;
import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTemplateDaoTest extends AbstractDaoConfig {

    @Autowired
    private InvoiceTemplateDao invoiceTemplateDao;

    @Test
    public void saveAndGet() {
        InvoiceTemplate expected = aNewEnhancedRandom().nextObject(InvoiceTemplate.class);
        invoiceTemplateDao.save(Collections.singletonList(expected));
        InvoiceTemplate actual = invoiceTemplateDao.get(expected.getInvoiceId(), expected.getInvoiceTemplateId());
        assertEquals(expected, actual);
    }

    @Test
    public void updatePreviousEventTest() {
        InvoiceTemplate expected = aNewEnhancedRandom().nextObject(InvoiceTemplate.class);
        invoiceTemplateDao.save(Collections.singletonList(expected));
        invoiceTemplateDao.save(Collections.singletonList(expected));
        InvoiceTemplate invoiceTemplateWithPreviousEventId = new InvoiceTemplate(expected);
        invoiceTemplateWithPreviousEventId.setEventId(expected.getEventId() - 1);
        invoiceTemplateDao.save(Collections.singletonList(invoiceTemplateWithPreviousEventId));
        InvoiceTemplate actual = invoiceTemplateDao.get(expected.getInvoiceId(), expected.getInvoiceTemplateId());
        assertEquals(expected, actual);
    }
}

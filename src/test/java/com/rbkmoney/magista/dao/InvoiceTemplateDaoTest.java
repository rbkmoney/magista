package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.InvoiceTemplateDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {InvoiceTemplateDaoImpl.class})
public class InvoiceTemplateDaoTest extends AbstractDaoTest {

    @Autowired
    private InvoiceTemplateDao invoiceTemplateDao;

    @Test
    public void saveAndGet() {
        InvoiceTemplate expected = aNewEnhancedRandom().nextObject(InvoiceTemplate.class);
        invoiceTemplateDao.save(Collections.singletonList(expected));
        InvoiceTemplate actual = invoiceTemplateDao.get(expected.getInvoiceId(), expected.getInvoiceTemplateId());
        Assert.assertEquals(expected, actual);
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

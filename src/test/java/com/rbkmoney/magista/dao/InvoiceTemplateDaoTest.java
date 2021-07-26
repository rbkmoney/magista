package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.testcontainers.annotations.postgresql.WithPostgresqlSingletonSpringBootITest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static com.rbkmoney.magista.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithPostgresqlSingletonSpringBootITest
public class InvoiceTemplateDaoTest {

    @Autowired
    private InvoiceTemplateDao invoiceTemplateDao;

    @Test
    public void saveAndGet() {
        Class<InvoiceTemplate> type = InvoiceTemplate.class;
        InvoiceTemplate expected = random(type);
        invoiceTemplateDao.save(Collections.singletonList(expected));
        InvoiceTemplate actual = invoiceTemplateDao.get(expected.getInvoiceTemplateId());
        assertEquals(expected, actual);
    }

    @Test
    public void updatePreviousEventTest() {
        InvoiceTemplate expected = random(InvoiceTemplate.class);
        invoiceTemplateDao.save(Collections.singletonList(expected));
        invoiceTemplateDao.save(Collections.singletonList(expected));
        InvoiceTemplate invoiceTemplateWithPreviousEventId = new InvoiceTemplate(expected);
        invoiceTemplateWithPreviousEventId.setEventId(expected.getEventId() - 1);
        invoiceTemplateDao.save(Collections.singletonList(invoiceTemplateWithPreviousEventId));
        InvoiceTemplate actual = invoiceTemplateDao.get(expected.getInvoiceTemplateId());
        assertEquals(expected, actual);
    }
}

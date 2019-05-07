package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.InvoiceDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {InvoiceDaoImpl.class})
public class InvoiceDaoTest extends AbstractDaoTest {

    @Autowired
    private InvoiceDao invoiceDao;

    @Test
    public void testInsertAndFindInvoiceData() {
        InvoiceData invoiceData = random(InvoiceData.class);

        invoiceDao.save(invoiceData);
        invoiceDao.save(invoiceData);

        assertEquals(invoiceData, invoiceDao.get(invoiceData.getInvoiceId()));
    }

}

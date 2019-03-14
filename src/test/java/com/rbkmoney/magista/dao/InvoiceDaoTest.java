package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.dao.impl.InvoiceDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;
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

        invoiceDao.saveInvoiceData(invoiceData);

        assertEquals(invoiceData, invoiceDao.getInvoiceData(invoiceData.getInvoiceId()));

        InvoiceEvent invoiceEvent = random(InvoiceEvent.class, "invoiceId");
        invoiceEvent.setInvoiceId(invoiceData.getInvoiceId());

        invoiceDao.saveInvoiceEvent(invoiceEvent);
    }

}

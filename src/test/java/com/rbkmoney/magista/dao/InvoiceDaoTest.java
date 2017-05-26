package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.model.Invoice;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 25/05/2017.
 */
public class InvoiceDaoTest extends AbstractIntegrationTest {

    @Autowired
    InvoiceDao invoiceDao;

    @Test
    public void insertUpdateAndFindInvoiceTest() throws IOException {
        Invoice invoice = random(Invoice.class);

        invoice.setDescription("\u0000\u0000\u0014stman description");

        invoiceDao.insert(invoice);

        invoice.setStatus(InvoiceStatus._Fields.PAID);

        invoiceDao.update(invoice);

        invoice.getDescription().replace("\u0000", "\\u0000");

        assertEquals(invoice, invoiceDao.findById(invoice.getId()));
    }
}

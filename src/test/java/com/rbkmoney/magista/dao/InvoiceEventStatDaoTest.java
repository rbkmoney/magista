package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class InvoiceEventStatDaoTest extends AbstractIntegrationTest {

    @Autowired
    InvoiceEventDao invoiceEventDao;

    @Test
    public void insertUpdateAndFindInvoiceEventTest() throws IOException {
        InvoiceEventStat invoiceEventStat = random(InvoiceEventStat.class);

        invoiceEventStat.setEventCategory(InvoiceEventCategory.INVOICE);

        invoiceEventDao.insert(invoiceEventStat);

        assertEquals(invoiceEventStat, invoiceEventDao.findInvoiceById(invoiceEventStat.getInvoiceId()));

        invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);

        invoiceEventDao.update(invoiceEventStat);

        assertEquals(invoiceEventStat, invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceEventStat.getInvoiceId(), invoiceEventStat.getPaymentId()));
    }

}

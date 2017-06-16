package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Test
    public void insertOnlyNotNullFields() throws DaoException {
        InvoiceEventStat invoiceEventStat = new InvoiceEventStat();
        invoiceEventStat.setEventId(Long.MIN_VALUE);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_CREATED);
        invoiceEventStat.setEventCategory(InvoiceEventCategory.INVOICE);
        invoiceEventStat.setEventCreatedAt(LocalDateTime.now());
        invoiceEventStat.setPartyId(UUID.randomUUID().toString());
        invoiceEventStat.setPartyShopId(Integer.MAX_VALUE);
        invoiceEventStat.setInvoiceId("-- \u0000");
        invoiceEventStat.setInvoiceStatus(InvoiceStatus.unpaid);
        invoiceEventStat.setInvoiceProduct("\000");
        invoiceEventStat.setInvoiceCurrencyCode("RUB");
        invoiceEventStat.setInvoiceAmount(Long.MAX_VALUE);
        invoiceEventStat.setInvoiceDue(LocalDateTime.now());
        invoiceEventStat.setInvoiceCreatedAt(LocalDateTime.now());

        invoiceEventDao.insert(invoiceEventStat);

        invoiceEventDao.findInvoiceById(invoiceEventStat.getInvoiceId());
    }

    @Test
    public void insertNullSymbolInString() throws IOException {
        InvoiceEventStat invoiceEventStat = random(InvoiceEventStat.class);

        invoiceEventStat.setInvoiceDescription("\u0000\u0000\u0014stman description");
        invoiceEventStat.setPaymentToken("\u0000kek\u0000eke\u0000");

        invoiceEventDao.insert(invoiceEventStat);
    }

}

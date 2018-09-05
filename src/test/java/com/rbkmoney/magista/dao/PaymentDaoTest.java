package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class PaymentDaoTest extends AbstractIntegrationTest {

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void insertAndFindPaymentDataTest() {
        InvoiceData invoiceData = random(InvoiceData.class, "id");
        invoiceDao.saveInvoiceData(invoiceData);
        invoiceDao.saveInvoiceData(invoiceData);

        PaymentData paymentData = random(PaymentData.class, "id", "invoiceId");
        paymentData.setInvoiceId(invoiceData.getInvoiceId());

        paymentDao.savePaymentData(paymentData);
        paymentDao.savePaymentData(paymentData);

        PaymentEvent paymentEvent = random(PaymentEvent.class, "invoiceId", "paymentId");
        paymentEvent.setInvoiceId(paymentData.getInvoiceId());
        paymentEvent.setPaymentId(paymentData.getPaymentId());

        paymentDao.savePaymentEvent(paymentEvent);

        assertEquals(paymentEvent, paymentDao.getPaymentEvent(paymentData.getInvoiceId(), paymentData.getPaymentId()));
    }

    @Test(expected = DaoException.class)
    public void testWhenSavePaymentEventWithoutInvoiceData() {
        PaymentEvent paymentEvent = random(PaymentEvent.class);
        paymentDao.savePaymentEvent(paymentEvent);
    }

}

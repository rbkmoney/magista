package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.InvoiceDaoImpl;
import com.rbkmoney.magista.dao.impl.PaymentDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {InvoiceDaoImpl.class, PaymentDaoImpl.class})
public class PaymentDaoTest extends AbstractDaoTest {

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

        assertEquals(paymentEvent, paymentDao.getLastPaymentEvent(paymentData.getInvoiceId(), paymentData.getPaymentId()));
    }

}

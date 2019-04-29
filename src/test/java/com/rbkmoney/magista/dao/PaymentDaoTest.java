package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.InvoiceDaoImpl;
import com.rbkmoney.magista.dao.impl.PaymentDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
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
        InvoiceData invoiceData = random(InvoiceData.class);
        invoiceDao.save(invoiceData);
        invoiceDao.save(invoiceData);

        PaymentData paymentData = random(PaymentData.class, "invoiceId");
        paymentData.setInvoiceId(invoiceData.getInvoiceId());

        paymentDao.save(paymentData);
        paymentDao.save(paymentData);

        assertEquals(paymentData, paymentDao.get(paymentData.getInvoiceId(), paymentData.getPaymentId()));
    }

}

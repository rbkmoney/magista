package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 25/05/2017.
 */
public class PaymentDaoTest extends AbstractIntegrationTest {

    @Autowired
    PaymentDao paymentDao;

    @Test
    public void insertUpdateAndFindPaymentTest() throws IOException {
        Payment payment = random(Payment.class);

        paymentDao.insert(payment);

        payment.setStatus(InvoicePaymentStatus._Fields.CAPTURED);

        assertEquals(payment, paymentDao.findById(payment.getId(), payment.getInvoiceId()));
    }

}

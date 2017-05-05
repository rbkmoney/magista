package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.model.Payment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 04/05/2017.
 */
public class PaymentDaoTest extends AbstractIntegrationTest {

    @Autowired
    PaymentDao paymentDao;

    @Test
    public void insertAndFindPaymentTest() throws IOException {
        Payment payment = new Payment();
        payment.setId("1");
        payment.setInvoiceId(";--'qwe");
        payment.setEventId(Long.MAX_VALUE);
        payment.setMerchantId(UUID.randomUUID().toString());
        payment.setStatus(InvoicePaymentStatus._Fields.PENDING);
        payment.setPaymentSystem(BankCardPaymentSystem.mastercard);
        payment.setMaskedPan("1234");
        payment.setCurrencyCode("RUB");
        payment.setAmount(100L);
        payment.setFee(50L);
        payment.setMaskedPan("6666");
        payment.setEmail("mamkin_haker@mail.ru");
        payment.setPhoneNumber("02");
        payment.setCityId(2);
        payment.setCountryId(3);
        payment.setFingerprint("ng4u3gnu4i3g44jri2rj32p3rj23iorjirewfk34jgoi");
        payment.setIp("localhost");
        Instant now = Instant.now();
        payment.setCreatedAt(now);
        payment.setChangedAt(now);
        payment.setModel(new MockTBaseProcessor().process(new InvoicePayment(),
                new TBaseHandler<>(InvoicePayment.class)));

        paymentDao.insert(payment);

        assertEquals(payment,
                paymentDao.findById(payment.getId(), payment.getInvoiceId()));
    }

    @Test
    public void insertTwoPaymentsWithEqualsPaymentsIdTest() throws IOException {
        Payment payment = new Payment();
        payment.setId("1");
        payment.setInvoiceId("keksik");
        payment.setEventId(1);
        payment.setMerchantId(UUID.randomUUID().toString());
        payment.setStatus(InvoicePaymentStatus._Fields.PENDING);
        payment.setPaymentSystem(BankCardPaymentSystem.mastercard);
        payment.setMaskedPan("1234");
        payment.setCurrencyCode("RUB");
        Instant now = Instant.now();
        payment.setCreatedAt(now);
        payment.setChangedAt(now);
        payment.setModel(new MockTBaseProcessor().process(new InvoicePayment(),
                new TBaseHandler<>(InvoicePayment.class)));
        paymentDao.insert(payment);

        payment.setEventId(2);
        payment.setStatus(InvoicePaymentStatus._Fields.CAPTURED);
        payment.setChangedAt(Instant.now());
        paymentDao.insert(payment);

        paymentDao.findById(payment.getId(), payment.getInvoiceId());
    }

}

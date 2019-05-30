package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.PaymentDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomStreamOf;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {PaymentDaoImpl.class})
public class PaymentDaoTest extends AbstractDaoTest {

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void insertAndFindPaymentDataTest() {
        PaymentData paymentData = random(PaymentData.class);

        paymentDao.save(List.of(paymentData));
        paymentDao.save(List.of(paymentData));

        assertEquals(paymentData, paymentDao.get(paymentData.getInvoiceId(), paymentData.getPaymentId()));
    }

    @Test
    public void batchUpsertTest() {
        String invoiceId = "invoiceId";
        String paymentId = "paymentId";

        List<PaymentData> payments = Stream.concat(
                randomStreamOf(100, PaymentData.class),
                randomStreamOf(100, PaymentData.class)
                        .map(
                                paymentData -> {
                                    paymentData.setInvoiceId(invoiceId);
                                    paymentData.setPaymentId(paymentId);
                                    return paymentData;
                                }
                        )
        ).collect(Collectors.toList());

        paymentDao.save(payments);
        assertEquals(payments.get(payments.size() - 1), paymentDao.get(invoiceId, paymentId));
    }

    @Test
    public void batchEmptyListTest() {
        paymentDao.save(List.of());
    }

}

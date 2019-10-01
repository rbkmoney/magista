package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.PaymentDaoImpl;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomStreamOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@ContextConfiguration(classes = {PaymentDaoImpl.class})
public class PaymentDaoTest extends AbstractDaoTest {

    @Autowired
    private PaymentDao paymentDao;

    @Test
    public void insertAndFindPaymentDataTest() {
        PaymentData paymentData = random(PaymentData.class);

        paymentDao.insert(List.of(paymentData));
        paymentDao.insert(List.of(paymentData));
        paymentDao.update(List.of(paymentData));
        paymentDao.update(List.of(paymentData));

        assertEquals(paymentData, paymentDao.get(paymentData.getInvoiceId(), paymentData.getPaymentId()));
    }

    @Test
    public void updatePreviousEventTest() {
        PaymentData paymentData = random(PaymentData.class);

        paymentDao.insert(List.of(paymentData));
        paymentDao.update(List.of(paymentData));

        PaymentData paymentDataWithPreviousEventId = new PaymentData(paymentData);
        paymentDataWithPreviousEventId.setEventId(paymentData.getEventId() - 1);

        paymentDao.update(List.of(paymentDataWithPreviousEventId));
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
                .sorted(Comparator.comparing(PaymentData::getEventId))
        ).collect(Collectors.toList());

        paymentDao.insert(payments);
        paymentDao.insert(payments);
        paymentDao.update(payments);
        paymentDao.update(payments);
        assertEquals(payments.get(payments.size() - 1), paymentDao.get(invoiceId, paymentId));
    }

    @Test
    public void batchInsertWithDifferentValues() {
        PaymentData paymentData = new PaymentData();
        paymentData.setInvoiceId(random(String.class));
        paymentData.setPaymentId(random(String.class));
        paymentData.setPartyId(UUID.randomUUID());
        paymentData.setPartyShopId(random(String.class));
        paymentData.setEventId(1L);
        paymentData.setEventCreatedAt(LocalDateTime.now());
        paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);
        paymentData.setPaymentCurrencyCode("RUB");
        paymentData.setPaymentOriginAmount(random(Long.class));
        paymentData.setPaymentAmount(random(Long.class));
        paymentData.setPaymentStatus(InvoicePaymentStatus.pending);
        paymentData.setPaymentDomainRevision(1L);
        paymentData.setPaymentTool(PaymentTool.payment_terminal);
        paymentData.setPaymentTerminalProvider("");
        paymentData.setPaymentFlow(PaymentFlow.instant);
        paymentData.setPaymentCreatedAt(LocalDateTime.now());
        paymentData.setPaymentPayerType(PaymentPayerType.payment_resource);


        PaymentData secondPaymentData = new PaymentData(paymentData);
        secondPaymentData.setPaymentFlow(PaymentFlow.hold);
        secondPaymentData.setPaymentHoldOnExpiration(OnHoldExpiration.cancel);
        secondPaymentData.setPaymentHoldUntil(LocalDateTime.now());
        secondPaymentData.setPaymentContext(new byte[] {0, 1, 2});


        List<PaymentData> payments = List.of(
                paymentData,
                secondPaymentData
        );

        paymentDao.insert(payments);
    }

    @Test
    public void batchEmptyListTest() {
        paymentDao.insert(List.of());
    }

}

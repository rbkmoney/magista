package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.model.EventCategory;
import com.rbkmoney.magista.model.InvoiceEvent;
import com.rbkmoney.magista.model.Payment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static com.rbkmoney.magista.model.EventCategory.PAYMENT;
import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 10/05/2017.
 */
public class InvoiceEventDaoTest extends AbstractIntegrationTest {


    @Autowired
    InvoiceEventDao invoiceEventDao;

    @Test
    public void insertAndFindPaymentTest() throws IOException {
        InvoiceEvent invoiceEvent = new InvoiceEvent();
        invoiceEvent.setMerchantId(UUID.randomUUID().toString());
        invoiceEvent.setShopId(424242);
        invoiceEvent.setEventId(Long.MAX_VALUE);
        invoiceEvent.setEventCategory(EventCategory.PAYMENT);
        invoiceEvent.setEventType(EventType.INVOICE_CREATED);
        invoiceEvent.setEventCreatedAt(Instant.now());
        invoiceEvent.setInvoiceId("; --");
        invoiceEvent.setInvoiceStatus(InvoiceStatus._Fields.UNPAID);
        invoiceEvent.setInvoiceAmount(Long.MIN_VALUE);
        invoiceEvent.setInvoiceCurrencyCode("RUB");
        invoiceEvent.setInvoiceCreatedAt(Instant.now());
        invoiceEvent.setPaymentId("5");
        invoiceEvent.setPaymentStatus(InvoicePaymentStatus._Fields.CAPTURED);
        invoiceEvent.setPaymentAmount(Long.MAX_VALUE);
        invoiceEvent.setPaymentFee(4242);
        invoiceEvent.setPaymentCountryId(-1);
        invoiceEvent.setPaymentCityId(-1);
        invoiceEvent.setPaymentEmail("mamkin_hacker@kek.ru");
        invoiceEvent.setPaymentIp("localhost");
        invoiceEvent.setPaymentFingerprint("ewjrpo23j34gj34gj34g");
        invoiceEvent.setPaymentPhoneNumber("89999999999");
        invoiceEvent.setPaymentMaskedPan("9999");
        invoiceEvent.setPaymentCreatedAt(Instant.now());

        invoiceEventDao.insert(invoiceEvent);

        assertEquals(invoiceEvent, invoiceEventDao.findPaymentByInvoiceAndPaymentId(invoiceEvent.getInvoiceId(), invoiceEvent.getPaymentId()));
    }

    @Test
    public void insertTwoPaymentsWithEqualsPaymentsIdTest() {
        InvoiceEvent invoiceEvent = new InvoiceEvent();
        invoiceEvent.setMerchantId(UUID.randomUUID().toString());
        invoiceEvent.setShopId(424242);
        invoiceEvent.setEventId(1);
        invoiceEvent.setEventCategory(EventCategory.INVOICE);
        invoiceEvent.setEventType(EventType.INVOICE_CREATED);
        invoiceEvent.setEventCreatedAt(Instant.now());
        invoiceEvent.setInvoiceId("; --");
        invoiceEvent.setInvoiceStatus(InvoiceStatus._Fields.UNPAID);
        invoiceEvent.setInvoiceAmount(Long.MIN_VALUE);
        invoiceEvent.setInvoiceCurrencyCode("RUB");
        invoiceEvent.setInvoiceCreatedAt(Instant.now());

        invoiceEventDao.insert(invoiceEvent);

        invoiceEvent.setEventId(2);
        invoiceEvent.setEventType(EventType.INVOICE_STATUS_CHANGED);
        invoiceEvent.setInvoiceStatus(InvoiceStatus._Fields.PAID);

        invoiceEventDao.insert(invoiceEvent);

        assertEquals(invoiceEvent, invoiceEventDao.findInvoiceById(invoiceEvent.getInvoiceId()));
    }

}

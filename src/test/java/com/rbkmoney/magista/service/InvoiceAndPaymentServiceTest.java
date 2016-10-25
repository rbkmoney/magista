package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoicePaid;
import com.rbkmoney.damsel.domain.InvoicePaymentCaptured;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.handler.Handler;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by tolkonepiu on 25/10/2016.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvoiceAndPaymentServiceTest {

    @Autowired
    List<Handler> handlers;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    PaymentService paymentService;

    List<StockEvent> stockEvents;

    @Before
    public void setup() throws IOException, TException {
        stockEvents = new ArrayList<>();
        TDeserializer tDeserializer = new TDeserializer(new TJSONProtocol.Factory());
        ClassPathResource resource = new ClassPathResource("data/bm_events.data");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            for (String line : br.lines().collect(Collectors.toList())) {
                SourceEvent sourceEvent = new SourceEvent();
                Event event = new Event();
                tDeserializer.deserialize(event, line.getBytes());
                sourceEvent.setProcessingEvent(event);
                stockEvents.add(new StockEvent(sourceEvent));
            }

        }
    }

    @Test
    public void serviceAndHandlersTest() {

        assertEquals(stockEvents.size(), 51);

        for (StockEvent stockEvent : stockEvents) {
            for (Handler handler : handlers) {
                if (handler.accept(stockEvent)) {
                    handler.handle(stockEvent);
                }
            }
        }
        assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, "mst.invoice"), 8);
        assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, "mst.payment"), 7);
        assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, "mst.customer"), 3);

        String invoiceId = "l6Op9zzxtw";

        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", invoice.getMerchantId());
        assertEquals(43, invoice.getEventId());
        assertEquals(450000, invoice.getAmount());
        assertEquals(InvoiceStatus._Fields.UNPAID, invoice.getStatus());
        assertEquals(Instant.from(TemporalConverter.stringToTemporal("2016-10-25T13:15:49.884332Z")), invoice.getCreatedAt());

        Instant instant = Instant.now();
        invoiceService.changeInvoiceStatus(invoiceId, 43, InvoiceStatus.paid(new InvoicePaid()), instant);

        invoice = invoiceService.getInvoiceById(invoiceId);
        assertEquals(InvoiceStatus._Fields.PAID, invoice.getStatus());
        assertEquals(instant, invoice.getChangedAt());

        Payment payment = paymentService.getPaymentByIds("1", invoiceId);
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", payment.getMerchantId());
        assertEquals("1", payment.getShopId());
        assertEquals("90b3bd52129ff2a40277445e02b85df3", payment.getCustomerId());
        assertEquals(InvoicePaymentStatus._Fields.CANCELLED, payment.getStatus());

        paymentService.changePaymentStatus("1", invoiceId, 44, InvoicePaymentStatus.captured(new InvoicePaymentCaptured()), instant);
        payment = paymentService.getPaymentByIds("1", invoiceId);
        assertEquals(InvoicePaymentStatus._Fields.CAPTURED, payment.getStatus());
        assertEquals(instant, payment.getChangedAt());
    }

    @After
    public void after() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "mst.invoice", "mst.payment", "mst.customer");
    }


}

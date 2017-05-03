package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.model.Invoice;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by tolkonepiu on 03/05/2017.
 */
public class InvoiceDaoTest extends AbstractIntegrationTest {

    @Autowired
    InvoiceDao invoiceDao;

    @Test
    public void insertAndFindInvoiceTest() throws IOException {
        Invoice invoice = new Invoice();
        invoice.setId("--'!QxSxc");
        invoice.setMerchantId(UUID.randomUUID().toString());
        invoice.setEventId(Long.MIN_VALUE);
        invoice.setShopId(Integer.MAX_VALUE);
        invoice.setAmount(100);
        invoice.setCurrencyCode("RUB");
        invoice.setStatus(InvoiceStatus._Fields.UNPAID);
        invoice.setCreatedAt(Instant.now());
        invoice.setModel(new MockTBaseProcessor().process(new com.rbkmoney.damsel.domain.Invoice(),
                new TBaseHandler<>(com.rbkmoney.damsel.domain.Invoice.class)));

        invoiceDao.insert(invoice);
    }

}

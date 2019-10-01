package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.InvoiceDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomStreamOf;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {InvoiceDaoImpl.class})
public class InvoiceDaoTest extends AbstractDaoTest {

    @Autowired
    private InvoiceDao invoiceDao;

    @Test
    public void testInsertAndFindInvoiceData() {
        InvoiceData invoiceData = random(InvoiceData.class);

        invoiceDao.insert(List.of(invoiceData));
        invoiceDao.insert(List.of(invoiceData));
        invoiceDao.update(List.of(invoiceData));
        invoiceDao.update(List.of(invoiceData));

        assertEquals(invoiceData, invoiceDao.get(invoiceData.getInvoiceId()));
    }

    @Test
    public void updatePreviousEventTest() {
        InvoiceData invoiceData = random(InvoiceData.class);

        invoiceDao.insert(List.of(invoiceData));
        invoiceDao.update(List.of(invoiceData));

        InvoiceData invoiceDataWithPreviousEventId = new InvoiceData(invoiceData);
        invoiceDataWithPreviousEventId.setEventId(invoiceData.getEventId() - 1);

        invoiceDao.update(List.of(invoiceDataWithPreviousEventId));
        assertEquals(invoiceData, invoiceDao.get(invoiceData.getInvoiceId()));
    }

    @Test
    public void testBatchUpsert() {
        String invoiceId = "invoiceId";

        List<InvoiceData> invoices = randomStreamOf(100, InvoiceData.class)
                        .map(
                                invoiceData -> {
                                    invoiceData.setInvoiceId(invoiceId);
                                    return invoiceData;
                                }
                        )
                .sorted(Comparator.comparing(InvoiceData::getEventId))
                .collect(Collectors.toList());

        invoiceDao.insert(invoices);
        invoiceDao.insert(invoices);
        invoiceDao.update(invoices);
        invoiceDao.update(invoices);
        assertEquals(invoices.get(invoices.size() - 1), invoiceDao.get(invoiceId));
    }

}

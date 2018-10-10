package com.rbkmoney.magista.query.impl.search;

import com.rbkmoney.damsel.merch_stat.StatInvoice;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.TokenUtil;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;

@Transactional
@Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
public class InvoiceSearchQueryTest extends AbstractIntegrationTest {

    @Test
    public void testInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'invoices': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testContinuationTokenWithInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'size': 1}}}";
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());
        DamselUtil.toJson(statResponse);

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }

    @Test
    public void testIfNotPresentInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','shop_id': '2', 'invoice_status': 'paid', 'payment_id': '1', 'payment_status': 'captured', 'from_time': '2017-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    public void testOrderByEventIdInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getInvoices().size());
        Instant instant = Instant.MAX;
        for (StatInvoice statInvoice : statResponse.getData().getInvoices()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statInvoice.getCreatedAt()));
            assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
        DamselUtil.toJson(statResponse);
    }

    @Test
    public void testInvoicesWithoutMerchantAndShopId() {
        String json = "{'query': {'invoices': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_domain_revision_search_data.sql")
    public void testSearchByPaymentDomainRevision() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'payment_domain_revision': 1, 'size': 1}}}";
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(0, statResponse.getData().getInvoices().size());
        json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'payment_domain_revision': 2, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from_payment_domain_revision': 1, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'to_payment_domain_revision': 1, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(0, statResponse.getData().getInvoices().size());
        json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from_payment_domain_revision': 1, 'to_payment_domain_revision': 2, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
    }

    @Sql("classpath:data/sql/search/recurrent_payments_search_data.sql")
    public void testRecurrentPayments() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

}

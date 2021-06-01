package com.rbkmoney.magista.query.impl.search;

import com.rbkmoney.damsel.domain.AllocationTransaction;
import com.rbkmoney.damsel.merch_stat.StatInvoice;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.AbstractQueryTest;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.service.TokenGenService;
import com.rbkmoney.magista.util.DamselUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
public class InvoiceSearchQueryTest extends AbstractQueryTest {

    @Autowired
    private TokenGenService tokenGenService;

    @Test
    public void testInvoices() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getInvoices().size());
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
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'size': 1}}}";
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertNotNull(statResponse.getContinuationToken());
        final LocalDateTime localDateTime = tokenGenService.extractTime(statResponse.getContinuationToken()).get();
        //assertEquals((Long) 9L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());
        DamselUtil.toJson(statResponse);

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertNotNull(statResponse.getContinuationToken());
        //assertEquals((Long) 8L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertNotNull(statResponse.getContinuationToken());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken("3gOc9TNJDkE1dOoK5oy6bJvgShunXxk2rTZuCn3SBts=1560155771740");
        queryProcessor.processQuery(statRequest);
    }

    @Test
    public void testIfNotPresentInvoices() {
        String json =
                "{'query': {'invoices': {'merchant_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','shop_id': '2', 'invoice_status': 'paid', 'payment_id': '1', 'payment_status': 'captured', 'from_time': '2017-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    public void testOrderByEventIdInvoices() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getInvoices().size());
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
        String json =
                "{'query': {'invoices': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(6, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_domain_revision_search_data.sql")
    public void testSearchByPaymentDomainRevision() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'payment_domain_revision': 1, 'size': 1}}}";
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(0, statResponse.getData().getInvoices().size());
        json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'payment_domain_revision': 2, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from_payment_domain_revision': 1, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
        json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'to_payment_domain_revision': 1, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(0, statResponse.getData().getInvoices().size());
        json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from_payment_domain_revision': 1, 'to_payment_domain_revision': 2, 'size': 1}}}";
        statRequest = new StatRequest(json);
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getInvoices().size());
    }

    @Test
    @Sql("classpath:data/sql/search/recurrent_payments_search_data.sql")
    public void testRecurrentPayments() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    public void testSearchByShopIds() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_ids': ['SHOP_ID']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getInvoices().size());
    }

    @Test
    public void testSearchByMultipleShopIds() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'B9C3BF7F-F62A-4675-8489-2DA7775024BB','shop_ids': ['12345', '6789']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getInvoices().size());
    }

    @Test(expected = QueryParserException.class)
    public void testDuplicateShopIdParam() {
        String json =
                "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'shop_ids': ['SHOP_ID']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
    }

    @Test(expected = QueryParserException.class)
    public void testShopIdsWithoutMerchantId() {
        String json = "{'query': {'invoices': {'shop_ids': ['SHOP_ID']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testSearchByInvoiceIds() {
        String json = "{'query': {'invoices': {'invoice_ids': ['INVOICE_NEW_ID_3', 'INVOICE_NEW_ID_4']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getInvoices().size());
    }

    @Test
    public void testSearchByBankCardTokenProvider() {
        String json = "{'query': {'invoices': {'payment_token_provider': 'applepay'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getInvoices().size());
    }

    @Test
    public void testSearchByInvoiceIdsWithAllocation() {
        String json = "{'query': {'invoices': {'invoice_ids': ['INVOICE_NEW_ID_4']}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getInvoices().size());
        Optional<StatInvoice> statInvoiceOptional = statResponse.getData().getInvoices().stream().findFirst();
        assertTrue(statInvoiceOptional.isPresent());
        List<AllocationTransaction> allocationTransactions = statInvoiceOptional.get().getAllocation().getTransactions();
        assertEquals(3, allocationTransactions.size());
        Optional<AllocationTransaction> foundedAllocationOptional = allocationTransactions.stream()
                .filter(allocationTransaction -> allocationTransaction.getId().equals("TEST_ALLOCATION_ID3"))
                .findFirst();
        assertTrue(foundedAllocationOptional.isPresent());
        AllocationTransaction allocationTransaction = foundedAllocationOptional.get();
        assertEquals("TEST_ALLOCATION_ID3", allocationTransaction.getId());
        assertEquals("testOwnerId3", allocationTransaction.getTarget().getShop().getOwnerId());
        assertEquals("testShopId3", allocationTransaction.getTarget().getShop().getShopId());
        assertEquals(8000L, allocationTransaction.getAmount().getAmount());
        assertEquals("RUB", allocationTransaction.getAmount().getCurrency().getSymbolicCode());
        assertEquals("testFeeTargetOwnerId", allocationTransaction.getBody().getFeeTarget().getShop().getOwnerId());
        assertEquals("testFeeTargetShopId", allocationTransaction.getBody().getFeeTarget().getShop().getShopId());
        assertEquals(1500, allocationTransaction.getBody().getFeeAmount().getAmount());
        assertEquals("RUB", allocationTransaction.getBody().getFeeAmount().getCurrency().getSymbolicCode());
        assertEquals(15, allocationTransaction.getBody().getFee().getParts().getP());
        assertEquals(1, allocationTransaction.getBody().getFee().getParts().getQ());
        assertEquals(9500, allocationTransaction.getBody().getTotal().getAmount());
        assertEquals("RUB", allocationTransaction.getBody().getTotal().getCurrency().getSymbolicCode());
    }

}

package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.TokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.rbkmoney.damsel.merch_stat.TerminalPaymentProvider.euroset;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Transactional
public class PaymentSearchQueryTest extends AbstractIntegrationTest {

    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPayments() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        System.out.println(statResponse);
        assertEquals(2, statResponse.getData().getPayments().size());
        DamselUtil.toJson(statResponse);
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'payments': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testContinuationTokenWithPayments() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'size': 1}}}";
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getPayments().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());
        DamselUtil.toJson(statResponse);

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getPayments().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testOrderByEventIdPayments() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getPayments().size());
        Instant instant = Instant.MAX;
        for (StatPayment statPayment : statResponse.getData().getPayments()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statPayment.getCreatedAt()));
            Assert.assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_operation_timeout_search_data.sql")
    public void testOperationTimeout() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-28T18:10:10Z', 'payment_status': 'failed', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertTrue(statResponse.getData().getPayments().stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetOperationTimeout()
        ));
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_holds_search_data.sql")
    public void testHoldAndInstantFlow() {
        //capture
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_1', 'payment_flow': 'hold', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.capture)
                        )
        );
        DamselUtil.toJson(statResponse);

        //cancel
        json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_2', 'payment_flow': 'hold', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.cancel)
                        )
        );
        DamselUtil.toJson(statResponse);

        //instant
        json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_3', 'payment_flow': 'instant', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetInstant()
                        )
        );
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPaymentsWithoutMerchantAndShopId() {
        String json = "{'query': {'payments': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getPayments().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_terminal_provider_search_data.sql")
    public void testFindByPaymentMethodAndTerminalProvider() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_method': 'payment_terminal', 'payment_terminal_provider':'euroset', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getPayments().size());
        assertEquals(euroset, statResponse.getData().getPayments().get(0).getPayer().getPaymentResource().getPaymentTool().getPaymentTerminal().getTerminalType());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_customer_id_search_data.sql")
    public void testFindByCustomerId() {
        String json = "{'query': {'payments': {'payment_customer_id': 'test', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getPayments().size());
        assertEquals("test", statResponse.getData().getPayments().get(0).getPayer().getCustomer().getCustomerId());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_external_failure_search_data.sql")
    public void testExternalFailure() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_status': 'failed', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-28T18:10:10Z', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertTrue(statResponse.getData().getPayments().stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetFailure()
        ));
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/recurrent_payments_search_data.sql")
    public void testRecurrentPayments() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'invoice_id': 'INVOICE_ID_1', 'payment_id': 'PAYMENT_ID_1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getPayments().size());

        StatPayment statPayment = statResponse.getData().getPayments().get(0);
        assertTrue(statPayment.isIsRecurring());
        RecurrentTokenSource tokenSource = statPayment.getRecurrentIntention().getTokenSource();
        assertTrue(tokenSource.isSetPayment());
        assertEquals("PARENT_INVOICE_ID_1", tokenSource.getPayment().getInvoiceId());
        assertEquals("PARENT_PAYMENT_ID_1", tokenSource.getPayment().getPaymentId());
        DamselUtil.toJson(statResponse);
    }

}

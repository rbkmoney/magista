package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.OnHoldExpiration;
import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.rbkmoney.damsel.merch_stat.TerminalPaymentProvider.euroset;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

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
    public void testPayments() throws TException {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getPayments().size());
        assertEquals(2, statResponse.getTotalCount());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testOrderByEventIdPayments() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getPayments().size());
        Instant instant = Instant.MAX;
        for (StatPayment statPayment : statResponse.getData().getPayments()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statPayment.getCreatedAt()));
            Assert.assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
    }

    @Test
    @Sql("classpath:data/sql/search/payment_operation_timeout_search_data.sql")
    public void testOperationTimeout() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-28T18:10:10Z', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayments().stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetOperationTimeout()
        ));
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_holds_search_data.sql")
    public void testHoldAndInstantFlow() {
        //capture
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_1', 'payment_flow': 'hold', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.capture)
                        )
        );

        //cancel
        json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_2', 'payment_flow': 'hold', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.cancel)
                        )
        );

        //instant
        json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_id': 'PAYMENT_ID_3', 'payment_flow': 'instant', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .allMatch(
                                payment -> payment.getFlow().isSetInstant()
                        )
        );
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPaymentsWithoutMerchantAndShopId() throws TException {
        String json = "{'query': {'payments': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getTotalCount());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/payment_terminal_provider_search_data.sql")
    public void testFindByPaymentMethodAndTerminalProvider() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'payment_method': 'payment_terminal', 'payment_terminal_provider':'euroset', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals(euroset, statResponse.getData().getPayments().get(0).getPayer().getPaymentResource().getPaymentTool().getPaymentTerminal().getTerminalType());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_customer_id_search_data.sql")
    public void testFindByCustomerId() {
        String json = "{'query': {'payments': {'payment_customer_id': 'test', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals("test", statResponse.getData().getPayments().get(0).getPayer().getCustomer().getCustomerId());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_external_failure_search_data.sql")
    public void testExternalFailure() {
        String json = "{'query': {'payments': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-28T18:10:10Z', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayments().stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetFailure()
        ));
    }


}

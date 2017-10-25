package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.OnHoldExpiration;
import com.rbkmoney.damsel.merch_stat.StatInvoice;
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
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

import static com.rbkmoney.damsel.merch_stat.TerminalPaymentProvider.euroset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by vpankrashkin on 29.08.16.
 */

@Sql("classpath:data/sql/invoices_and_payments_test_data.sql")
@Transactional
public class QueryProcessorImplTest extends AbstractIntegrationTest {
    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    public void testInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getInvoices().size());
        assertEquals(5, statResponse.getTotalCount());
    }

    @Test
    public void testOrderByEventIdInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(5, statResponse.getData().getInvoices().size());
        Instant instant = Instant.MAX;
        for (StatInvoice statInvoice : statResponse.getData().getInvoices()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statInvoice.getCreatedAt()));
            assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
    }

    @Test
    public void testPayouts() {
        String json = "{'query': {'payouts': {'merchant_id': '281220eb-a4ef-4d03-b666-bdec4b26c5f7', 'shop_id': '1507555501740', 'from_time': '2016-10-25T15:45:20Z','to_time': '2018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals(1, statResponse.getData().getPayouts().size());
        assertEquals("281220eb-a4ef-4d03-b666-bdec4b26c5f7", statResponse.getData().getPayouts().get(0).getPartyId());
        assertEquals("1507555501740", statResponse.getData().getPayouts().get(0).getShopId());
    }

    @Test
    public void testPayments() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getPayments().size());
        assertEquals(3, statResponse.getTotalCount());
    }

    @Test
    public void testOperationTimeout() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-28T18:10:10Z', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayments().stream().anyMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetOperationTimeout()
        ));
    }

    @Test
    public void testHolds() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '2','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .anyMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.cancel)
                        )
        );
        assertTrue(
                statResponse.getData().getPayments().stream()
                        .anyMatch(
                                payment -> payment.getFlow().isSetHold()
                                        && payment.getFlow().getHold().getOnHoldExpiration().equals(OnHoldExpiration.capture)
                        )
        );
    }

    @Test
    public void testShopCategoryIds() {
        String json = "{'query': {'payments': {'shop_category_ids': [4, 5], 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(4, statResponse.getData().getPayments().size());
        assertEquals(4, statResponse.getTotalCount());
    }

    @Test
    public void testFindByFlow() {
        // holds in payment
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '2', 'payment_flow': 'hold', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream().allMatch(
                        payment -> payment.getFlow().isSetHold()
                )
        );
        assertEquals(2, statResponse.getTotalCount());

        // instant in payment
        json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '2', 'payment_flow': 'instant', 'from_time': '2016-10-21T15:45:20Z','to_time': '2016-10-28T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(json);
        assertTrue(
                statResponse.getData().getPayments().stream().allMatch(
                        payment -> payment.getFlow().isSetInstant()
                )
        );
        assertEquals(1, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsWithoutMerchantAndShopId() {
        String json = "{'query': {'payments': {'from_time': '2015-10-25T15:45:20Z','to_time': '2017-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(9, statResponse.getTotalCount());
    }

    @Test
    public void testInvoicesWithoutMerchantAndShopId() {
        String json = "{'query': {'invoices': {'from_time': '2015-10-25T15:45:20Z','to_time': '2017-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(8, statResponse.getTotalCount());
    }

    @Test
    public void testFindByPaymentMethodAndTerminalProvider() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1', 'payment_method': 'payment_terminal', 'payment_terminal_provider':'euroset', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals(euroset, statResponse.getData().getPayments().get(0).getPaymentTool().getPaymentTerminal().getTerminalType());
    }

    @Test
    public void testExternalFailure() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-28T18:10:10Z', 'from':'1', 'size':'1'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayments().stream().anyMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetExternalFailure()
        ));
    }

    @Test
    public void testOrderByEventIdPayments() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getPayments().size());
        Instant instant = Instant.MAX;
        for (StatPayment statPayment : statResponse.getData().getPayments()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statPayment.getCreatedAt()));
            assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
    }

    @Test
    public void testPaymentsTurnover() {
        String json = "{'query': {'payments_turnover': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsGeoStat() {
        String json = "{'query': {'payments_geo_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsCardTypesStat() {
        String json = "{'query': {'payments_pmt_cards_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsCardTypeWithEmptyPaymentSystem() throws TException {
        String json = "{'query': {'payments_pmt_cards_stat': {'merchant_id': '349d01f8-9349-4b50-b071-567c8204ff63','shop_id': '1','from_time': '2015-10-25T15:45:20Z','to_time': '2018-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals("{\"data\":{\"records\":[]}}", new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse));
    }

    @Test
    public void testPaymentsConversionStat() {
        String json = "{'query': {'payments_conversion_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testCustomersRateStat() {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testAccountingReport() {
        String json = "{'query': {'shop_accounting_report': {'from_time': '2017-07-31T21:00:00Z','to_time': '2017-08-31T21:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        Map<String, String> result1 = statResponse.getData().getRecords().stream().filter(
                t ->
                        t.get("merchant_id").equals("test_party_1")
                                && t.get("shop_id").equals("test_shop_1")
        ).findFirst().get();
        assertEquals(9, result1.size());
        assertEquals("test_party_1", result1.get("merchant_id"));
        assertEquals("test_shop_1", result1.get("shop_id"));
        assertEquals("RUB", result1.get("currency_code"));
        assertEquals("0", result1.get("opening_balance"));
        assertEquals("3000", result1.get("funds_acquired"));
        assertEquals("75", result1.get("fee_charged"));
        assertEquals("950", result1.get("funds_paid_out"));
        assertEquals("1000", result1.get("funds_refunded"));
        assertEquals("975", result1.get("closing_balance"));
        assertEquals(0, statResponse.getTotalCount());

        String json2 = "{'query': {'shop_accounting_report': {'from_time': '2017-08-31T21:00:00Z','to_time': '2017-09-30T21:00:00Z'}}}";
        StatResponse statResponse2 = queryProcessor.processQuery(json2);
        assertEquals(1, statResponse2.getData().getRecords().size());
        Map<String, String> result2 = statResponse2.getData().getRecords().stream().filter(
                t ->
                        t.get("merchant_id").equals("test_party_1")
                                && t.get("shop_id").equals("test_shop_1")
        ).findFirst().get();
        assertEquals(9, result2.size());
        assertEquals("test_party_1", result2.get("merchant_id"));
        assertEquals("test_shop_1", result2.get("shop_id"));
        assertEquals("RUB", result2.get("currency_code"));
        assertEquals("975", result2.get("opening_balance"));
        assertEquals("6000", result2.get("funds_acquired"));
        assertEquals("150", result2.get("fee_charged"));
        assertEquals("2875", result2.get("funds_paid_out"));
        assertEquals("2000", result2.get("funds_refunded"));
        assertEquals("1950", result2.get("closing_balance"));
        assertEquals(0, statResponse2.getTotalCount());

        String json3 = "{'query': {'shop_accounting_report': {'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse3 = queryProcessor.processQuery(json3);
        assertEquals(4, statResponse3.getData().getRecords().size());
        Map<String, String> result3 = statResponse3.getData().getRecords().stream().filter(
                t ->
                        t.get("merchant_id").equals("74480e4f-1a36-4edd-8175-7a9e984313b0")
                                && t.get("shop_id").equals("1")
        ).findFirst().get();
        assertEquals(9, result3.size());
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result3.get("merchant_id"));
        assertEquals("1", result3.get("shop_id"));
        assertEquals("RUB", result3.get("currency_code"));
        assertEquals("444000", result3.get("funds_acquired"));
        assertEquals("19980", result3.get("fee_charged"));
        assertEquals("2259530", result3.get("opening_balance"));
        assertEquals("0", result3.get("funds_paid_out"));
        assertEquals("0", result3.get("funds_refunded"));
        assertEquals("2683550", result3.get("closing_balance"));

        Map<String, String> result4 = statResponse3.getData().getRecords().stream().filter(
                t ->
                        t.get("merchant_id").equals("74480e4f-1a36-4edd-8175-7a9e984313b0")
                                && t.get("shop_id").equals("2")
        ).findFirst().get();
        assertEquals(9, result4.size());
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result4.get("merchant_id"));
        assertEquals("2", result4.get("shop_id"));
        assertEquals("RUB", result4.get("currency_code"));
        assertEquals("3631200", result4.get("funds_acquired"));
        assertEquals("163403", result4.get("fee_charged"));
        assertEquals("0", result4.get("opening_balance"));
        assertEquals("0", result4.get("funds_paid_out"));
        assertEquals("0", result4.get("funds_refunded"));
        assertEquals("3467797", result4.get("closing_balance"));

        assertEquals(0, statResponse3.getTotalCount());

    }

}


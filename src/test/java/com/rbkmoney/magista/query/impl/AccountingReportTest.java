package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AccountingReportTest extends AbstractIntegrationTest {

    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    @Sql("classpath:data/sql/invoices_and_payments_test_data.sql")
    public void testAccountingHappyCase() {
        String json = "{'query': {'shop_accounting_report': {'merchant_id': 'test_party_1', 'shop_id': 'test_shop_1', 'currency_code': 'RUB', 'to_time': '2017-08-31T21:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getRecords().size());
        Map<String, String> result1 = statResponse.getData().getRecords().get(0);
        assertEquals(8, result1.size());
        assertEquals("test_party_1", result1.get("merchant_id"));
        assertEquals("test_shop_1", result1.get("shop_id"));
        assertEquals("RUB", result1.get("currency_code"));
        assertEquals("3000", result1.get("funds_acquired"));
        assertEquals("75", result1.get("fee_charged"));
        assertEquals("2", result1.get("funds_adjusted"));
        assertEquals("950", result1.get("funds_paid_out"));
        assertEquals("1000", result1.get("funds_refunded"));
        assertEquals(0, statResponse.getTotalCount());

        String json2 = "{'query': {'shop_accounting_report': {'merchant_id': 'test_party_1', 'shop_id': 'test_shop_1', 'currency_code': 'RUB', 'from_time': '2017-08-31T21:00:00Z','to_time': '2017-09-30T21:00:00Z'}}}";
        StatResponse statResponse2 = queryProcessor.processQuery(new StatRequest(json2));
        assertEquals(1, statResponse2.getData().getRecords().size());
        Map<String, String> result2 = statResponse2.getData().getRecords().get(0);
        assertEquals(8, result2.size());
        assertEquals("test_party_1", result2.get("merchant_id"));
        assertEquals("test_shop_1", result2.get("shop_id"));
        assertEquals("RUB", result2.get("currency_code"));
        assertEquals("6000", result2.get("funds_acquired"));
        assertEquals("150", result2.get("fee_charged"));
        assertEquals("0", result2.get("funds_adjusted"));
        assertEquals("2875", result2.get("funds_paid_out"));
        assertEquals("2000", result2.get("funds_refunded"));
        assertEquals(0, statResponse2.getTotalCount());

        String json3 = "{'query': {'shop_accounting_report': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0', 'shop_id': '1', 'currency_code': 'RUB', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse3 = queryProcessor.processQuery(new StatRequest(json3));
        assertEquals(1, statResponse3.getData().getRecords().size());
        Map<String, String> result3 = statResponse3.getData().getRecords().get(0);
        assertEquals(8, result3.size());
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result3.get("merchant_id"));
        assertEquals("1", result3.get("shop_id"));
        assertEquals("RUB", result3.get("currency_code"));
        assertEquals("444000", result3.get("funds_acquired"));
        assertEquals("19980", result3.get("fee_charged"));
        assertEquals("0", result3.get("funds_adjusted"));
        assertEquals("0", result3.get("funds_paid_out"));
        assertEquals("0", result3.get("funds_refunded"));

        String json4 = "{'query': {'shop_accounting_report': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0', 'shop_id': '2', 'currency_code': 'RUB', 'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse4 = queryProcessor.processQuery(new StatRequest(json4));
        assertEquals(1, statResponse4.getData().getRecords().size());
        Map<String, String> result4 = statResponse4.getData().getRecords().get(0);
        assertEquals(8, result4.size());
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result4.get("merchant_id"));
        assertEquals("2", result4.get("shop_id"));
        assertEquals("RUB", result4.get("currency_code"));
        assertEquals("3631200", result4.get("funds_acquired"));
        assertEquals("163403", result4.get("fee_charged"));
        assertEquals("0", result4.get("funds_adjusted"));
        assertEquals("0", result4.get("funds_paid_out"));
        assertEquals("0", result4.get("funds_refunded"));

        assertEquals(0, statResponse3.getTotalCount());
    }

    @Test
    public void testWhenNotFound() {
        String json = "{'query': {'shop_accounting_report': {'merchant_id': 'not_found', 'shop_id': '404', 'currency_code': 'RUB', 'to_time': '2017-08-31T21:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getRecords().size());
        Map<String, String> result4 = statResponse.getData().getRecords().get(0);
        assertEquals(8, result4.size());
        assertEquals("not_found", result4.get("merchant_id"));
        assertEquals("404", result4.get("shop_id"));
        assertEquals("RUB", result4.get("currency_code"));
        assertEquals("0", result4.get("funds_acquired"));
        assertEquals("0", result4.get("fee_charged"));
        assertEquals("0", result4.get("funds_adjusted"));
        assertEquals("0", result4.get("funds_paid_out"));
        assertEquals("0", result4.get("funds_refunded"));
    }

}

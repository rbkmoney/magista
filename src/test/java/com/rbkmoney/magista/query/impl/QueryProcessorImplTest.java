package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by vpankrashkin on 29.08.16.
 */

@Sql("classpath:data/sql/invoice_event_stat_test_data.sql")
@Transactional
public class QueryProcessorImplTest extends AbstractIntegrationTest {
    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    public void testInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getInvoices().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPayments() {
        String json = "{'query': {'payments': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getPayments().size());
        assertEquals(0, statResponse.getTotalCount());
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
    public void testPaymentsConversionStat() {
        String json = "{'query': {'payments_conversion_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testCustomersRateStat() {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    @Ignore
    public void testAccountingReport() {
        String json = "{'query': {'shop_accounting_report': {'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());

        Map<String, String> result1 = statResponse.getData().getRecords().get(0);
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result1.get("merchant_id"));
        assertEquals("1", result1.get("shop_id"));
        assertEquals("444000", result1.get("funds_acquired"));
        assertEquals("19980", result1.get("fee_charged"));
        assertEquals("2259530", result1.get("opening_balance"));
        assertEquals("2683550", result1.get("closing_balance"));

        Map<String, String> result2 = statResponse.getData().getRecords().get(1);
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result2.get("merchant_id"));
        assertEquals("2", result2.get("shop_id"));
        assertEquals("3631200", result2.get("funds_acquired"));
        assertEquals("163403", result2.get("fee_charged"));
        assertEquals("0", result2.get("opening_balance"));
        assertEquals("3467797", result2.get("closing_balance"));

        Map<String, String> result3 = statResponse.getData().getRecords().get(2);
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", result3.get("merchant_id"));
        assertEquals("3", result3.get("shop_id"));
        assertEquals("450000", result3.get("funds_acquired"));
        assertEquals("20250", result3.get("fee_charged"));
        assertEquals("0", result3.get("opening_balance"));
        assertEquals("429750", result3.get("closing_balance"));

        assertEquals(0, statResponse.getTotalCount());
    }

}



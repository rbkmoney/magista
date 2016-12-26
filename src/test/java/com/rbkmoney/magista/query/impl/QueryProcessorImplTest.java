package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by vpankrashkin on 29.08.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:data/sql/invoices_and_payments_test_data.sql")
public class QueryProcessorImplTest {
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
        String json = "{'query': {'payments_turnover': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsGeoStat() {
        String json = "{'query': {'payments_geo_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsCardTypesStat() {
        String json = "{'query': {'payments_pmt_cards_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsConversionStat() {
        String json = "{'query': {'payments_conversion_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testCustomersRateStat() {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '2','from_time': '2016-10-25T15:41:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testAccountingReport() {
        String json = "{'query': {'shop_accounting_report': {'from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());
        assertEquals("74480e4f-1a36-4edd-8175-7a9e984313b0", statResponse.getData().getRecords().get(0).get("merchant_id"));
        assertEquals("19980", statResponse.getData().getRecords().get(0).get("fee_charged"));
        assertEquals("2259530", statResponse.getData().getRecords().get(0).get("opening_balance"));
        assertEquals("2683550", statResponse.getData().getRecords().get(0).get("closing_balance"));
        assertEquals(0, statResponse.getTotalCount());
    }

    @After
    public void after() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "mst.invoice", "mst.payment", "mst.customer");
    }
}



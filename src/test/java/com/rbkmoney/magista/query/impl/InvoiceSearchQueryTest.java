package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatInvoice;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
public class InvoiceSearchQueryTest extends AbstractIntegrationTest {

    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    public void testInvoices() throws TException {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getInvoices().size());
        assertEquals(2, statResponse.getTotalCount());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    public void testIfNotPresentInvoices() throws TException {
        String json = "{'query': {'invoices': {'merchant_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','shop_id': '2', 'invoice_status': 'paid', 'payment_id': '1', 'payment_status': 'captured', 'from_time': '2017-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getInvoices().size());
        assertEquals(0, statResponse.getTotalCount());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    public void testOrderByEventIdInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32','shop_id': 'SHOP_ID', 'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(2, statResponse.getData().getInvoices().size());
        Instant instant = Instant.MAX;
        for (StatInvoice statInvoice : statResponse.getData().getInvoices()) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statInvoice.getCreatedAt()));
            assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
    }

    @Test
    public void testInvoicesWithoutMerchantAndShopId() throws TException {
        String json = "{'query': {'invoices': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getTotalCount());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

}

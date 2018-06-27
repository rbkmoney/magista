package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.*;
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

import static com.rbkmoney.damsel.merch_stat.PayoutStatus._Fields.*;
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
    public void testPayouts() throws TException {
        String json = "{'query': {'payouts': {'merchant_id': '281220eb-a4ef-4d03-b666-bdec4b26c5f7', 'shop_id': '1507555501740', 'from_time': '2016-10-25T15:45:20Z','to_time': '2018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals(1, statResponse.getData().getPayouts().size());
        assertEquals("281220eb-a4ef-4d03-b666-bdec4b26c5f7", statResponse.getData().getPayouts().get(0).getPartyId());
        assertEquals("1507555501740", statResponse.getData().getPayouts().get(0).getShopId());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    public void testDuplicatePayouts() {
        String json = "{'query': {'payouts': {'merchant_id': 'test_party_1', 'shop_id': 'test_shop_1', 'from_time': '2016-10-25T15:45:20Z','to_time': '2019-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(5, statResponse.getTotalCount());
    }

    @Test
    public void testPayoutStatuses() {
        String json = "{'query': {'payouts': {'payout_statuses': ['paid', 'cancelled', 'confirmed'] }}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayouts().stream().allMatch(
                payout -> payout.getStatus().getSetField() == PAID
                        || payout.getStatus().getSetField() == CANCELLED
                        || payout.getStatus().getSetField() == CONFIRMED
        ));

        json = "{'query': {'payouts': {'payout_statuses': ['paid', 'cancelled'] }}}";
        statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayouts().stream().allMatch(
                payout -> payout.getStatus().getSetField() == PAID
                        || payout.getStatus().getSetField() == CANCELLED
        ));

        json = "{'query': {'payouts': {'payout_statuses': ['paid', 'confirmed'] }}}";
        statResponse = queryProcessor.processQuery(json);
        assertTrue(statResponse.getData().getPayouts().stream().allMatch(
                payout -> payout.getStatus().getSetField() == PAID
                        || payout.getStatus().getSetField() == CONFIRMED
        ));
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
    public void testPaymentsWithEmptyGeoStat() throws TException {
        String json = "{'query': {'payments_geo_stat': {'merchant_id': '7e77621d-6f43-46b5-9e61-188363def06b','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(0, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals("{\"data\":{\"records\":[]}}", new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse));
    }

    @Test
    public void testPaymentsCardTypesStat() {
        String json = "{'query': {'payments_pmt_cards_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());
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
    public void testRefunds() throws TException {
        String json = "{'query': {'refunds': {'from_time': '2015-10-25T15:45:20Z','to_time': '2017-10-26T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(4, statResponse.getTotalCount());
        assertEquals(4, statResponse.getData().getRefunds().size());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);

        json = "{'query': {'refunds': {'refund_id':'test_refund_2', 'from_time': '2015-10-25T15:45:20Z','to_time': '2017-10-26T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getTotalCount());
        assertEquals(1, statResponse.getData().getRefunds().size());
        StatRefund statRefund = statResponse.getData().getRefunds().get(0);
        assertEquals("test_refund_2", statRefund.getId());
        new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse);
    }

    @Test
    public void testCustomersRateStat() {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '74480e4f-1a36-4edd-8175-7a9e984313b0','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(3, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testCustomersRateStatWithEmptyFingerprint() throws TException {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '7e77621d-6f43-46b5-9e61-188363def06b','shop_id': '1','from_time': '2016-10-25T15:45:20Z','to_time': '2016-10-25T18:10:10Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals("{\"data\":{\"records\":[{\"unic_count\":\"0\",\"offset\":\"180\"}]}}", new TSerializer(new TSimpleJSONProtocol.Factory()).toString(statResponse));
    }

}

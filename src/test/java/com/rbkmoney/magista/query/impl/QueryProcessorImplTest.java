package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryProcessorImplTest {
    private QueryProcessorImpl queryProcessor;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(new StatisticsDaoTest());
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(),contextFactory);
    }

    @Test
    public void testInvoices() {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '1','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getInvoices().size());
        assertEquals(2, statResponse.getTotalCount());
    }

    @Test
    public void testPayments() {
        String json = "{'query': {'payments': {'merchant_id': '42','shop_id': '45','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getPayments().size());
        assertEquals(2, statResponse.getTotalCount());
    }

    @Test
    public void testPaymentsTurnover() {
        String json = "{'query': {'payments_turnover': {'merchant_id': '42','shop_id': '45','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60', 'from':'1', 'size':'2'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals(PaymentsTurnoverStatFunction.FUNC_NAME, statResponse.getData().getRecords().get(0).get(StatisticsDaoTest.KEY));
    }

    @Test
    public void testPaymentsGeoStat() {
        String json = "{'query': {'payments_geo_stat': {'merchant_id': '42','shop_id': '45','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals(PaymentsGeoStatFunction.FUNC_NAME, statResponse.getData().getRecords().get(0).get(StatisticsDaoTest.KEY));
    }

    @Test
    public void testPaymentsConversionStat() {
        String json = "{'query': {'payments_conversion_stat': {'merchant_id': '42','shop_id': '45','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T16:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals(PaymentsConversionStatFunction.FUNC_NAME, statResponse.getData().getRecords().get(0).get(StatisticsDaoTest.KEY));
    }

    @Test
    public void testCustomersRateStat() {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '42','shop_id': '45','from_time': '2016-08-11T00:12:00Z','to_time': '2016-08-11T17:12:00Z', 'split_interval':'60'}}}";
        StatResponse statResponse = queryProcessor.processQuery(json);
        assertEquals(1, statResponse.getData().getRecords().size());
        assertEquals(0, statResponse.getTotalCount());
        assertEquals(CustomersRateStatFunction.FUNC_NAME, statResponse.getData().getRecords().get(0).get(StatisticsDaoTest.KEY));
    }

    private static class StatisticsDaoTest implements StatisticsDao {
        public static final String KEY = "KEY";

        @Override
        public Collection<Invoice> getInvoices(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> invoiceStatus, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
            return Arrays.asList(new Invoice());
        }

        @Override
        public int getInvoicesCount(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> invoiceStatus, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
            return 2;
        }

        @Override
        public Collection<Payment> getPayments(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> paymentId, Optional<String> paymentStatus, Optional<String> panMask, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
            return Arrays.asList(new Payment());
        }

        @Override
        public Integer getPaymentsCount(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> paymentId, Optional<String> paymentStatus, Optional<String> panMask, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
            return 2;
        }

        @Override
        public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
            return getMaps(merchantId, shopId, fromTime, toTime, splitInterval, PaymentsTurnoverStatFunction.FUNC_NAME);
        }

        @Override
        public Collection<Map<String, String>> getPaymentsGeoStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
             return getMaps(merchantId, shopId, fromTime, toTime, splitInterval, PaymentsGeoStatFunction.FUNC_NAME);
        }

        @Override
        public Collection<Map<String, String>> getPaymentsConversionStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
            return getMaps(merchantId, shopId, fromTime, toTime, splitInterval, PaymentsConversionStatFunction.FUNC_NAME);
        }

        @Override
        public Collection<Map<String, String>> getCustomersRateStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
            return getMaps(merchantId, shopId, fromTime, toTime, splitInterval, CustomersRateStatFunction.FUNC_NAME);
        }

        protected Collection<Map<String, String>> getMaps(final String merchantId, final String shopId, final Instant fromTime, final Instant toTime, final int splitInterval, String key) {
            return Arrays.asList(new HashMap<String, String>(){{
                put(merchantId, merchantId);
                put(shopId, shopId);
                put(fromTime+"", fromTime+"");
                put(toTime+"", toTime+"");
                put(splitInterval+"", splitInterval+"");
                put(KEY, key);
            }});
        }
    }
}



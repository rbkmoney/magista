package com.rbkmoney.magista.query.impl.report;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.query.AbstractQueryTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AccountingReportTest extends AbstractQueryTest {

    @Test
    @Sql("classpath:data/sql/report/case1_report_data.sql")
    public void testHappyCase() {
        String json = "{'query': {'shop_accounting_report': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'shop_id': 'test_shop_1', 'currency_code': 'RUB', 'to_time': '2017-08-31T21:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getRecords().size());
        Map<String, String> result = statResponse.getData().getRecords().get(0);
        assertEquals(8, result.size());
        assertEquals("db79ad6c-a507-43ed-9ecf-3bbd88475b32", result.get("merchant_id"));
        assertEquals("test_shop_1", result.get("shop_id"));
        assertEquals("RUB", result.get("currency_code"));
        assertEquals("3000", result.get("funds_acquired"));
        assertEquals("75", result.get("fee_charged"));
        assertEquals("2", result.get("funds_adjusted"));
        assertEquals("950", result.get("funds_paid_out"));
        assertEquals("1000", result.get("funds_refunded"));
        assertEquals(0, statResponse.getTotalCount());

        json = "{'query': {'shop_accounting_report': {'merchant_id': 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'shop_id': 'test_shop_1', 'currency_code': 'RUB', 'from_time': '2017-08-31T21:00:00Z','to_time': '2017-09-30T21:00:00Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getRecords().size());
        result = statResponse.getData().getRecords().get(0);
        assertEquals(8, result.size());
        assertEquals("db79ad6c-a507-43ed-9ecf-3bbd88475b32", result.get("merchant_id"));
        assertEquals("test_shop_1", result.get("shop_id"));
        assertEquals("RUB", result.get("currency_code"));
        assertEquals("6000", result.get("funds_acquired"));
        assertEquals("150", result.get("fee_charged"));
        assertEquals("0", result.get("funds_adjusted"));
        assertEquals("2875", result.get("funds_paid_out"));
        assertEquals("2000", result.get("funds_refunded"));
        assertEquals(0, statResponse.getTotalCount());
    }

    @Test
    public void testWhenNotFound() {
        String json = "{'query': {'shop_accounting_report': {'merchant_id': 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'shop_id': '404', 'currency_code': 'RUB', 'to_time': '2017-08-31T21:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(1, statResponse.getData().getRecords().size());
        Map<String, String> result4 = statResponse.getData().getRecords().get(0);
        assertEquals(8, result4.size());
        assertEquals("DB79AD6C-A507-43ED-9ECF-3BBD88475B32", result4.get("merchant_id"));
        assertEquals("404", result4.get("shop_id"));
        assertEquals("RUB", result4.get("currency_code"));
        assertEquals("0", result4.get("funds_acquired"));
        assertEquals("0", result4.get("fee_charged"));
        assertEquals("0", result4.get("funds_adjusted"));
        assertEquals("0", result4.get("funds_paid_out"));
        assertEquals("0", result4.get("funds_refunded"));
    }

}

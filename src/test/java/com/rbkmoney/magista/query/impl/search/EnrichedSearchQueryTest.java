package com.rbkmoney.magista.query.impl.search;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.query.AbstractQueryTest;
import com.rbkmoney.magista.util.DamselUtil;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
public class EnrichedSearchQueryTest extends AbstractQueryTest {

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedPayments() {
        String json = "{'query': {'enriched_payments': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getEnrichedInvoices().size());
        DamselUtil.toJson(statResponse);
    }


    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedRefunds() {
        String json = "{'query': {'enriched_refunds': {}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getEnrichedInvoices().size());
        DamselUtil.toJson(statResponse);
    }
}

package com.rbkmoney.magista.query.impl.search;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.config.testconfiguration.QueryProcessorConfig;
import com.rbkmoney.magista.query.QueryProcessor;
import com.rbkmoney.magista.util.DamselUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * merchant OKKO-specific, in general shouldn't be touched
 *
 * @author n.pospolita
 */
@PostgresqlSpringBootITest
@Import(QueryProcessorConfig.class)
public class EnrichedSearchQueryTest {

    @Autowired
    private QueryProcessor<StatRequest, StatResponse> queryProcessor;

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedPayments() {
        String json =
                "{'query': {'enriched_payments': {'from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(4, statResponse.getData().getEnrichedInvoices().size());
        assertEquals(3L, statResponse.getData().getEnrichedInvoices().stream()
                .filter(enrichedStatInvoice -> enrichedStatInvoice.refunds.size() > 0).count());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedRefunds() {
        String json = "{'query': {'enriched_refunds': {}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getEnrichedInvoices().size());
        DamselUtil.toJson(statResponse);
    }

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_time_test.sql")
    public void testNewTimeRanges() {
        String json =
                "{'query': {'enriched_payments': {'from_time': '3000-01-02T00:00:00Z','to_time': '3000-01-02T02:00:00Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(4, statResponse.getData().getEnrichedInvoices().size());
        assertEquals(3L, statResponse.getData().getEnrichedInvoices().stream()
                .filter(enrichedStatInvoice -> enrichedStatInvoice.refunds.size() > 0).count());
        DamselUtil.toJson(statResponse);
    }

}

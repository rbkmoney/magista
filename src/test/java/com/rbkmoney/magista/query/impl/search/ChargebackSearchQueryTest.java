package com.rbkmoney.magista.query.impl.search;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.config.AbstractQueryConfig;
import com.rbkmoney.magista.util.DamselUtil;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChargebackSearchQueryTest extends AbstractQueryConfig {

    @Test
    @Sql("classpath:data/sql/search/chargeback_search_data.sql")
    public void chargebackSearchTest() {
        String json =
                "{'query': {'chargebacks': {'merchant_id': 'party_id_1','shop_id': 'party_shop_id_1','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(3, statResponse.getData().getChargebacks().size());
        DamselUtil.toJson(statResponse);

        json =
                "{'query': {'chargebacks': {'chargeback_categories': ['fraud', 'dispute'], 'merchant_id': 'party_id_1','shop_id': 'party_shop_id_1','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getChargebacks().size());
        DamselUtil.toJson(statResponse);

        json =
                "{'query': {'chargebacks': {'chargeback_statuses': ['pending', 'cancelled'], 'merchant_id': 'party_id_1','shop_id': 'party_shop_id_1','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getChargebacks().size());
        DamselUtil.toJson(statResponse);

        json =
                "{'query': {'chargebacks': {'chargeback_stages': ['chargeback', 'arbitration'], 'merchant_id': 'party_id_1','shop_id': 'party_shop_id_1','from_time': '2016-10-25T15:45:20Z','to_time': '3018-10-25T18:10:10Z'}}}";
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(2, statResponse.getData().getChargebacks().size());
        DamselUtil.toJson(statResponse);
    }

}

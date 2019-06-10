package com.rbkmoney.magista.query.impl.builder;

import com.rbkmoney.magista.config.properties.TokenGenProperties;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.FunctionQueryContext;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.RootQuery;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.service.TokenGenService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public class QueryBuilderImplTest {
    JsonQueryParser parser = JsonQueryParser.newWeakJsonQueryParser();

    private QueryBuilder builder = new QueryBuilderImpl();

    private FunctionQueryContext queryContext;

    public QueryBuilderImplTest() {
        TokenGenProperties tokenGenPropertiesMock = mock(TokenGenProperties.class);
        when(tokenGenPropertiesMock.getKey()).thenReturn("jXnZr4u7x!A%D*G-KaPvSgVkYp3s5v8t/B?E(H+MbQeThWmZq4t7w9z$C&F)J@Nc");
        TokenGenService tokenGenService = new TokenGenService(tokenGenPropertiesMock);
        this.queryContext = mock(FunctionQueryContext.class);
        when(queryContext.getTokenGenService()).thenReturn(tokenGenService);
    }

    @Test
    public void test() {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        Query query = buildQuery(json);
        assertTrue(query instanceof RootQuery);
        query.getDescriptor();
    }

    @Test(expected = QueryBuilderException.class)
    public void testNoFunctionParse() {
        String json = "{'query': {'payments_geo_stat1': {}}}";
        Query query = buildQuery(json);
        fail("no functions in oot query, should not reach this point");
    }

    @Test
    public void testEnrichedFunctionsParse() {
        String json = "{'query': {'enriched_payments': {'from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        Query query = buildQuery(json);
        assertTrue(query instanceof RootQuery);
        query.getDescriptor();

        json = "{'query': {'enriched_refunds': {'from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        query = buildQuery(json);
        assertTrue(query instanceof RootQuery);
        query.getDescriptor();
    }

    Query buildQuery(String json) {
        List<QueryPart> queryParts = parser.parseQuery(json);
        return builder.buildQuery(queryContext, queryParts, null, null, null);
    }
}

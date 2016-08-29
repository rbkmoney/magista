package com.rbkmoney.magista.query.impl.builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.RootQuery;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query.parser.QueryPart;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public class QueryBuilderImplTest {
    JsonQueryParser parser = JsonQueryParser.newWeakJsonQueryParser();

    private QueryBuilder builder = new QueryBuilderImpl();

    @Test
    public void test() {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        Query query = buildQuery(json);
        assertTrue(query instanceof RootQuery);
        query.getDescriptor();
    }

    @Test(expected = QueryBuilderException.class)
    public void testNoFunctionParse() throws Exception {
        String json = "{'query': {'payments_geo_stat1': {}}}";
        Query query = buildQuery(json);
        fail("no functions in oot query, should not reach this point");
    }

    Query buildQuery(String json) {
        List<QueryPart> queryParts =  parser.parseQuery(json);
        return builder.buildQuery(queryParts, null, null);
    }
}

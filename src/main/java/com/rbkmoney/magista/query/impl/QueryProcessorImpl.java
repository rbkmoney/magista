package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.dsl.*;
import com.rbkmoney.magista.dsl.builder.QueryBuilder;
import com.rbkmoney.magista.dsl.parser.QueryParser;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryProcessorImpl implements QueryProcessor<StatRequest, StatResponse> {
    private QueryParser<String> sourceParser;
    private QueryBuilder queryBuilder;
    private QueryContextFactory queryContextFactory;

    public QueryProcessorImpl(QueryParser<String> sourceParser, QueryBuilder queryBuilder, QueryContextFactory queryContextFactory) {
        this.sourceParser = sourceParser;
        this.queryBuilder = queryBuilder;
        this.queryContextFactory = queryContextFactory;
    }

    @Override
    public StatResponse processQuery(StatRequest source) throws BadTokenException, QueryProcessingException {
        List<QueryPart> queryParts = sourceParser.parseQuery(source.getDsl(), null);
        Query query = queryBuilder.buildQuery(queryParts, source.getContinuationToken(), null, null);
        QueryContext queryContext = queryContextFactory.getContext();
        QueryResult queryResult = query.execute(queryContext);
        Object result = queryResult.getCollectedStream();
        if (result instanceof StatResponse) {
            return (StatResponse) result;
        } else {
            throw new QueryProcessingException("QueryResult has wrong type: " + result.getClass().getName());
        }
    }
}

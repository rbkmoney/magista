package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.query2.*;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query2.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParser;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryProcessorImpl implements QueryProcessor<String, StatResponse> {
    private QueryParser<String> sourceParser;
    private QueryBuilder queryBuilder;
    private QueryContextFactory queryContextFactory;

    public QueryProcessorImpl(QueryContextFactory queryContextFactory) {
        this(new JsonQueryParser(), new QueryBuilderImpl(), queryContextFactory);
    }

    public QueryProcessorImpl(QueryParser<String> sourceParser, QueryBuilder queryBuilder, QueryContextFactory queryContextFactory) {
        this.sourceParser = sourceParser;
        this.queryBuilder = queryBuilder;
        this.queryContextFactory = queryContextFactory;
    }

    @Override
    public StatResponse processQuery(String source) throws QueryProcessingException {
        List<QueryPart> queryParts = sourceParser.parseQuery(source, null);
        Query query = queryBuilder.buildQuery(queryParts, null, null);
        QueryContext queryContext = queryContextFactory.getContext();
        QueryResult queryResult = query.execute(queryContext);
        Object result = queryResult.getCollectedStream();
        if (result instanceof StatResponse) {
            return (StatResponse) result;
        } else {
            throw new QueryProcessingException("QueryResult has wrong type: "+ result.getClass().getName());
        }
    }
}

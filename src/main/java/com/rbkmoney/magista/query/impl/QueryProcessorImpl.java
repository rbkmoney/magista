package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query.parser.QueryParser;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.util.List;

public class QueryProcessorImpl implements QueryProcessor<StatRequest, StatResponse> {
    private QueryParser<String> sourceParser;
    private QueryBuilder queryBuilder;
    private QueryContextFactory queryContextFactory;

    public QueryProcessorImpl(QueryContextFactory queryContextFactory) {
        this(new JsonQueryParser(), new QueryBuilderImpl(), queryContextFactory);
    }

    public QueryProcessorImpl(QueryParser<String> sourceParser,
                              QueryBuilder queryBuilder,
                              QueryContextFactory queryContextFactory) {
        this.sourceParser = sourceParser;
        this.queryBuilder = queryBuilder;
        this.queryContextFactory = queryContextFactory;
    }

    @Override
    public StatResponse processQuery(StatRequest source) throws BadTokenException, QueryProcessingException {
        List<QueryPart> queryParts = sourceParser.parseQuery(source.getDsl(), null);
        QueryContext queryContext = queryContextFactory.getContext();
        Query query = queryBuilder.buildQuery(
                queryContext,
                queryParts,
                source.getContinuationToken(),
                null,
                null
        );
        QueryResult queryResult = query.execute(queryContext);
        Object result = queryResult.getCollectedStream();
        if (result instanceof StatResponse) {
            return (StatResponse) result;
        } else {
            throw new QueryProcessingException("QueryResult has wrong type: " + result.getClass().getName());
        }
    }
}

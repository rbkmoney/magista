package com.rbkmoney.magista.query2.parser;

import com.rbkmoney.magista.query2.CompositeQuery;
import com.rbkmoney.magista.query2.Query;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public abstract class BaseQueryBuilder implements QueryBuilder {


    @Override
    public Query buildQuery(Query parentQuery, List<QueryPart> queryParts) throws QueryBuilderException {
        try {
            List<QueryBuilder> builders = getQueryBuilders(queryParts);
            List<Query> queries = builders.stream().map(builder -> builder.buildQuery(parentQuery, queryParts)).collect(Collectors.toList());

            return new CompositeQuery(queries, null);
        } catch (QueryParserException e) {
            throw new QueryBuilderException("Failed to parse source: "+ queryParts, e);
        }
    }

    protected  List<QueryBuilder> getQueryBuilders(List<QueryPart> queryParts) {
        return null;
    }
}

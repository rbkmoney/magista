package com.rbkmoney.magista.query2.impl.builder;

import com.rbkmoney.magista.query2.BaseCompositeQuery;
import com.rbkmoney.magista.query2.CompositeQuery;
import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public abstract class AbstractQueryBuilder implements QueryBuilder {

    protected Stream<QueryPart> getMatchedPartsStream(Object descriptor, List<QueryPart> queryParts) {
        return queryParts.stream().filter(queryPart -> descriptor.equals(queryPart.getDescriptor()));
    }


    protected CompositeQuery createCompositeQuery(Object descriptor, QueryParameters derivedParameters, List<Query> childQueries, boolean parallel) {
        BaseCompositeQuery query = new BaseCompositeQuery(descriptor, new QueryParameters(Collections.emptyMap(), derivedParameters));
        query.setChildQueries(childQueries, parallel);
        return query;
    }

    protected CompositeQuery createCompositeQuery(Object descriptor, List<Query> childQueries, boolean parallel) {
        return createCompositeQuery(descriptor, new QueryParameters(Collections.emptyMap(), null), childQueries, parallel);

    }

    protected QueryParameters getParameters(QueryPart queryPart) {
        return queryPart == null ? null : queryPart.getParameters();
    }

    protected List<Query> buildQueries(Object matchDescriptor, List<QueryPart> queryParts, Function<QueryPart, Query> queryCreator) {
        List<QueryPart> matchedParts = getMatchedPartsStream(matchDescriptor, queryParts).collect(Collectors.toList());
        return matchedParts.stream().map(queryPart -> queryCreator.apply(queryPart)).collect(Collectors.toList());
    }

    protected Query buildAndWrapQueries(Object matchDescriptor, List<QueryPart> queryParts, Function<QueryPart, Query> queryCreator, QueryParameters parentParameters) {
        List<Query> queries = buildQueries(matchDescriptor, queryParts, queryCreator);
        Query resultQuery = queries.size() > 1 ? createCompositeQuery(queries.get(0).getDescriptor(), parentParameters, queries, false) : queries.get(0);
        return resultQuery;
    }
}

package com.rbkmoney.magista.query2.parser;

import com.rbkmoney.magista.query2.Query;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public interface QueryBuilder {
    default Query buildQuery(List<QueryPart> queryParts) throws QueryBuilderException {
        return buildQuery(null, queryParts);
    }

    Query buildQuery(Query parentQuery, List<QueryPart> queryParts) throws QueryBuilderException;
}

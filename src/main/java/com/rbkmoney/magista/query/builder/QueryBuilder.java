package com.rbkmoney.magista.query.builder;

import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public interface QueryBuilder {

    Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException;

    boolean apply(List<QueryPart> queryParts, QueryPart parent);
}

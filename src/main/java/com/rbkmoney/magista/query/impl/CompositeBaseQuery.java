package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.CompositeQuery;
import com.rbkmoney.magista.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public abstract class CompositeBaseQuery extends  BaseQuery implements CompositeQuery {
    private final List<Query> queries;
    private final boolean parallel;

    public CompositeBaseQuery(Map<String, Object> params, List<Query> queries, boolean parallel) {
        super(params);
        this.queries = queries;
        this.parallel = parallel;
    }

    public CompositeBaseQuery(Map<String, Object> params, List<Query> queries) {
        this(params, queries, false);
    }

    public List<Query> getQueries() {
        return queries;
    }

    public boolean isParallel() {
        return parallel;
    }
}

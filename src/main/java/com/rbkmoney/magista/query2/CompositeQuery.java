package com.rbkmoney.magista.query2;


import java.util.List;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class CompositeQuery extends BaseQuery {
    private final List<Query> queries;
    private final boolean parallel;

    public CompositeQuery(List<Query> queries, QueryParameters params, boolean parallel) {
        super(params);
        this.queries = queries;
        this.parallel = parallel;
    }

    public CompositeQuery(List<Query> queries, QueryParameters params) {
        this(queries, params, false);
    }

    public List<Query> getQueries() {
        return queries;
    }

    public boolean isParallel() {
        return parallel;
    }
}

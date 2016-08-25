package com.rbkmoney.magista.query2;


/**
 * Created by vpankrashkin on 04.08.16.
 */
public abstract class BaseQuery implements Query {
    private final Query parentQuery;
    private final QueryParameters queryParameters;

    public BaseQuery(QueryParameters params) {
        this(params, null);
    }

    public BaseQuery(QueryParameters params, Query parentQuery) {
        this.queryParameters = params;
        this.parentQuery = parentQuery;
    }

    @Override
    public Query getParentQuery() {
        return parentQuery;
    }

    @Override
    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    protected QueryParameters extractParameters(Query query) {
        return query == null ? null : query.getQueryParameters();
    }


}

package com.rbkmoney.magista.query2;


/**
 * Created by vpankrashkin on 04.08.16.
 */
public abstract class BaseQuery implements Query {
    private final Object descriptor;
    private QueryParameters queryParameters;
    private Query parentQuery;

    public BaseQuery(Object descriptor, QueryParameters params) {
        if (descriptor == null || params == null) {
            throw new NullPointerException("Null descriptor or params're not allowed");
        }
        this.descriptor = descriptor;
        this.queryParameters = createQueryParameters(params , params.getDerivedParameters());// y, this is bad
    }

    @Override
    public Object getDescriptor() {
        return descriptor;
    }

    @Override
    public Query getParentQuery() {
        return parentQuery;
    }

    @Override
    public void setParentQuery(Query query) {
        this.parentQuery = query;
        this.queryParameters = createQueryParameters(queryParameters, extractParameters(query));
    }

    @Override
    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    protected  QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new QueryParameters(parameters, derivedParameters);
    }

    protected QueryParameters extractParameters(Query query) {
        return query == null ? null : query.getQueryParameters();
    }


}

package com.rbkmoney.magista.query2;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface Query<T, CT> {
    Object getDescriptor();

    Query getParentQuery();

    void setParentQuery(Query query);

    QueryParameters getQueryParameters();

    QueryResult<T, CT> execute(QueryContext context) throws QueryExecutionException;


}

package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface Query {

    Query getParentQuery();

    Object getParameter(String key);

    Object getNestedParameter(String key);

    QueryResult compute(QueryContext context);
}

package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface Query {

    Object getParameter(String key);

    QueryResult compute(QueryContext context);
}

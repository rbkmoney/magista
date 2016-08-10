package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.Query;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class QueryContextBuilder {

    private Object dao;

    public QueryContextBuilder(Object dao) {
        this.dao = dao;
    }

    public FunctionQueryContext getQueryContext(Query query) {
        return new FunctionQueryContext(dao);

    }
}

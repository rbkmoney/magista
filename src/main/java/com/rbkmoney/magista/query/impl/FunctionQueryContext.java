package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryContext;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {
    private Object dao;

    public FunctionQueryContext(Object dao) {
        this.dao = dao;
    }

    public Object getDao() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

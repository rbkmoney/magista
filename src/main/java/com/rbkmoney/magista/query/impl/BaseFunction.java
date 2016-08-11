package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.FunctionQuery;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryExecutionException;
import com.rbkmoney.magista.query.QueryResult;

import java.util.Map;

/**
 * Created by vpankrashkin on 05.08.16.
 */
public abstract class BaseFunction extends BaseQuery implements FunctionQuery {
    private final Class resultElementType;
    private final String name;

    public BaseFunction(Map<String, Object> params, Class resultElementType, String name) {
        super(params);
        this.resultElementType = resultElementType;
        this.name = name;
    }

    @Override
    public Class getResultElementType() {
        return resultElementType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QueryResult execute(QueryContext context) throws QueryExecutionException {
        if (context instanceof FunctionQueryContext) {
            return execute((FunctionQueryContext) context);
        } else {
            throw new QueryExecutionException("Wrong context type");
        }
    }

    protected abstract QueryResult execute(FunctionQueryContext context);
}

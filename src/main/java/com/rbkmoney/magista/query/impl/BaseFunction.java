package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.FunctionQuery;

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
}

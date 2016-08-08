package com.rbkmoney.magista.dsl.impl;

import com.rbkmoney.magista.dsl.FunctionQuery;

import java.util.Map;

/**
 * Created by vpankrashkin on 05.08.16.
 */
public abstract class FunctionBaseQuery extends BaseQuery implements FunctionQuery {
    private final Class resultElementType;

    public FunctionBaseQuery(Map<String, Object> params, Class resultElementType) {
        super(params);
        this.resultElementType = resultElementType;
    }

    @Override
    public Class getResultElementType() {
        return resultElementType;
    }
}

package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.Query;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.temporal.TemporalAccessor;
import java.util.Map;


/**
 * Created by vpankrashkin on 04.08.16.
 */
public abstract class BaseQuery implements Query {
    private final Query parentQuery;
    protected final Map<String, Object> params;

    public BaseQuery(Map<String, Object> params) {
        this(params, null);
    }

    public BaseQuery(Map<String, Object> params, Query parentQuery) {
        this.params = params;
        this.parentQuery = parentQuery;
    }

    @Override
    public Query getParentQuery() {
        return parentQuery;
    }

    @Override
    public Object getParameter(String key) {
        return params.get(key);
    }

    @Override
    public Object getNestedParameter(String key) {
        Object val = params.get(key);
        return val != null ? val : parentQuery != null ? parentQuery.getNestedParameter(key) : null;
    }

    public Long getLongParameter(String key, boolean nested) {
        Object val = nested ? getNestedParameter(key) : getParameter(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        } else if (val instanceof String) {
            return Long.getLong(val.toString());
        } else {
            return null;
        }
    }

    public Integer getIntParameter(String key, boolean nested) {
        Object val = nested ? getNestedParameter(key) : getParameter(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val instanceof String) {
            return Integer.parseInt(val.toString());
        } else {
            return null;
        }
    }

    public String getStringParameter(String key, boolean nested) {
        Object val =  (nested ? getNestedParameter(key) : getParameter(key));
        return val != null ? val.toString() : null;
    }

    public TemporalAccessor getTimeParameter(String key, boolean nested) {
        Object val = nested ? getNestedParameter(key) : getParameter(key);
        if (val instanceof TemporalAccessor) {
            return (TemporalAccessor) val;
        } else if (val instanceof String) {
            return TemporalConverter.stringToTemporal(val.toString());
        } else {
            return null;
        }
    }

    void validateParameters() {
        checkParams(params, true);
    }

    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        if (params == null) {
            throw new IllegalArgumentException("Params're not defined");
        }
        return checkParamsResult(throwOnError, false, null);
    }

    public static boolean checkParamsResult(boolean throwOnError, boolean hasError, String msg) {
        if (throwOnError && hasError) {
            throw new IllegalArgumentException(msg);
        }
        return !hasError;
    }


}

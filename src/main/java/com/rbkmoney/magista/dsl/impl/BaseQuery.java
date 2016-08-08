package com.rbkmoney.magista.dsl.impl;

import com.rbkmoney.magista.dsl.Query;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by vpankrashkin on 04.08.16.
 */
public abstract class BaseQuery implements Query {
    protected final Map<String, Object> params;

    public BaseQuery(Map<String, Object> params) {
        validateParameters(params);
        this.params = Collections.unmodifiableMap(new HashMap<>(params));
    }

    @Override
    public Object getParameter(String key) {
        return params.get(key);
    }

    public Long getLongParameter(String key) {
        Object val = params.get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        } else if (val instanceof String) {
            return Long.getLong(val.toString());
        } else {
            return null;
        }
    }

    public Integer getIntParameter(String key) {
        Object val = params.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val instanceof String) {
            return Integer.getInteger(val.toString());
        } else {
            return null;
        }
    }

    public String getStringParameter(String key) {
        return String.valueOf(params.get(key));
    }

    public TemporalAccessor getTimeParameter(String key) {
        Object val = params.get(key);
        if (val instanceof TemporalAccessor) {
            return (TemporalAccessor) val;
        } else if (val instanceof String) {
            return TemporalConverter.stringToTemporal(val.toString());
        } else {
            return null;
        }
    }

    protected void validateParameters(Map<String, Object> params) {
        checkParams(params, true);
    }

    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return checkParamsResult(throwOnError, true, null);
    }

    public static boolean checkParamsResult(boolean throwOnError, boolean hasError, String msg) {
        if (throwOnError && hasError) {
            throw new IllegalArgumentException(msg);
        }
        return hasError;
    }


}

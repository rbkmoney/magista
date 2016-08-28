package com.rbkmoney.magista.query2;

import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public class QueryParameters {
    public interface QueryParametersRef<T extends QueryParameters> {
        T newInstance(Map<String, Object> parameters, QueryParameters derivedParameters);
    }

    private final Map<String, Object> parameters;
    private final QueryParameters derivedParameters;

    public QueryParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
        this.parameters = new HashMap<>(parameters);
        this.derivedParameters = derivedParameters;
    }

    public QueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        this(parameters.getParametersMap(), derivedParameters);
    }

    public Object getParameter(String key, boolean deepSearch) {
        return parameters.getOrDefault(key, deepSearch && derivedParameters != null ? derivedParameters.getParameter(key, deepSearch) : null);
    }

    public <T extends QueryParameters> T removeParameters(QueryParametersRef<T> parametersRef, String... keys) {
        Map<String, Object> newParameters = new HashMap<>(parameters);
        for (String key: keys) {
            newParameters.remove(key);
        }
        return parametersRef.newInstance(newParameters, derivedParameters);
    }

    public Long getLongParameter(String key, boolean deepSearch) throws IllegalArgumentException {

        Object val = getParameter(key, deepSearch);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        } else if (val instanceof String) {
            try {
                return Long.parseLong(val.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Field:"+key +" has incorrect value", e);
            }
        } else {
            return null;
        }
    }

    public Integer getIntParameter(String key, boolean deepSearch) throws IllegalArgumentException {
        Object val = getParameter(key, deepSearch);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val instanceof String) {
            try {
                return Integer.parseInt(val.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Field:"+key +" has incorrect value", e);
            }
        } else {
            return null;
        }
    }

    public String getStringParameter(String key, boolean deepSearch) {
        Object val =  getParameter(key, deepSearch);
        return val != null ? val.toString() : null;
    }

    public TemporalAccessor getTimeParameter(String key, boolean deepSearch) throws IllegalArgumentException {
        Object val = getParameter(key, deepSearch);
        if (val instanceof TemporalAccessor) {
            return (TemporalAccessor) val;
        } else if (val instanceof String) {
            return TemporalConverter.stringToTemporal(val.toString());
        } else {
            return null;
        }
    }

    public Map<String, Object> getParametersMap() {
        return parameters;
    }

    @Override
    public String toString() {
        return "QueryParameters{" +
                "parameters=" + parameters +
                ", derivedParameters=" + (derivedParameters == null ? "null" : "notnull") +
                '}';
    }
}

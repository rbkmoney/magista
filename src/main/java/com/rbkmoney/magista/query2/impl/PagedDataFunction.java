package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

import java.util.Map;

import static com.rbkmoney.magista.query2.impl.Parameters.FROM_PARAMETER;
import static com.rbkmoney.magista.query2.impl.Parameters.SIZE_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public abstract class PagedDataFunction extends ScopedFunction {
    private final PagedDataParameters parameters;

    public PagedDataFunction(QueryParameters params, Query parentQuery, String name) {
        super(params, parentQuery, name);
        this.parameters = new PagedDataParameters(params, extractParameters(parentQuery));

    }

    @Override
    public PagedDataParameters getQueryParameters() {
        return parameters;
    }

    public static class PagedDataParameters extends ScopedParameters {

        public PagedDataParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PagedDataParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public Integer getFrom() {
            return getIntParameter(FROM_PARAMETER, true);
        }

        public Integer getSize() {
            return getIntParameter(SIZE_PARAMETER, true);
        }

    }

    public static class PagedDataValidator extends ScopedValidator {

    }
}

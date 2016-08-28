package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

import java.util.Map;

import static com.rbkmoney.magista.query2.impl.Parameters.FROM_PARAMETER;
import static com.rbkmoney.magista.query2.impl.Parameters.SIZE_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public abstract class PagedBaseFunction extends ScopedBaseFunction {
    private final PagedBaseParameters parameters;

    public PagedBaseFunction(QueryParameters params, Query parentQuery, String name) {
        super(params, parentQuery, name);
        this.parameters = new PagedBaseParameters(params, extractParameters(parentQuery));

    }

    @Override
    public PagedBaseParameters getQueryParameters() {
        return parameters;
    }

    public static class PagedBaseParameters extends ScopedBaseParameters {

        public PagedBaseParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PagedBaseParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public Integer getFrom() {
            return getIntParameter(FROM_PARAMETER, true);
        }

        public Integer getSize() {
            return getIntParameter(SIZE_PARAMETER, true);
        }

    }

    public static class PagedBaseValidator extends ScopedBaseValidator {

    }
}

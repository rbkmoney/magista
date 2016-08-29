package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryParameters;

import java.util.Map;

import static com.rbkmoney.magista.query.impl.Parameters.FROM_PARAMETER;
import static com.rbkmoney.magista.query.impl.Parameters.SIZE_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public abstract class PagedBaseFunction<T ,CT> extends ScopedBaseFunction<T, CT> {

    public PagedBaseFunction(Object descriptor, QueryParameters params, String name) {
        super(descriptor, params, name);

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

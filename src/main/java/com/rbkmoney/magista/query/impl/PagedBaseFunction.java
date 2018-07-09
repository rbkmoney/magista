package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.query.QueryParameters;
import com.rbkmoney.magista.util.TokenUtil;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.magista.query.impl.Parameters.FROM_PARAMETER;
import static com.rbkmoney.magista.query.impl.Parameters.SIZE_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public abstract class PagedBaseFunction<T, CT> extends ScopedBaseFunction<T, CT> {

    private String continuationToken;

    public PagedBaseFunction(Object descriptor, QueryParameters params, String name, String continuationToken) {
        super(descriptor, params, name);
        this.continuationToken = continuationToken;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public Optional<Long> getFromId() {
        return TokenUtil.extractIdValue(continuationToken);
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
        @Override
        public void validateQuery(Query query) throws IllegalArgumentException {
            super.validateQuery(query);
            if (query instanceof PagedBaseFunction) {
                validateContinuationToken(query.getQueryParameters(), ((PagedBaseFunction) query).getContinuationToken());
            }
        }

        private void validateContinuationToken(QueryParameters queryParameters, String continuationToken) throws BadTokenException {
            try {
                TokenUtil.validateToken(queryParameters, continuationToken);
            } catch (IllegalArgumentException ex) {
                throw new BadTokenException("Token validation failure", ex);
            }
        }
    }
}

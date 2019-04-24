package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dsl.BadTokenException;
import com.rbkmoney.magista.dsl.Query;
import com.rbkmoney.magista.dsl.QueryParameters;
import com.rbkmoney.magista.dsl.TokenUtil;

import java.util.Map;
import java.util.Optional;

import static com.rbkmoney.magista.query.impl.Parameters.FROM_PARAMETER;
import static com.rbkmoney.magista.dsl.Parameters.SIZE_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public abstract class PagedBaseFunction<T, CT> extends ScopedBaseFunction<T, CT> {

    public static final int MAX_SIZE_VALUE = 1000;

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
            return Optional.ofNullable(getIntParameter(SIZE_PARAMETER, true))
                    .orElse(MAX_SIZE_VALUE);
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

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PagedBaseParameters pagedBaseParameters = super.checkParamsType(parameters, PagedBaseParameters.class);
            checkParamsResult(pagedBaseParameters.getSize() > MAX_SIZE_VALUE,
                    String.format(
                            "Size must be less or equals to %d but was %d",
                            MAX_SIZE_VALUE,
                            pagedBaseParameters.getSize()
                    )
            );
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

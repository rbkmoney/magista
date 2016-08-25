package com.rbkmoney.magista.query2;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public interface QueryValidator {
    void validateParameters(QueryParameters parameters) throws IllegalArgumentException;

    default void validateQuery(Query query) throws IllegalArgumentException {
        validateParameters(query.getQueryParameters());
    }
}

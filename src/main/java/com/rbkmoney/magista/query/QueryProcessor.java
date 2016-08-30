package com.rbkmoney.magista.query;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public interface QueryProcessor<S, R> {
    R processQuery(S source) throws QueryProcessingException;
}

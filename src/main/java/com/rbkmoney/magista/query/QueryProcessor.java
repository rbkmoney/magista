package com.rbkmoney.magista.query2;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public interface QueryProcessor<S, R> {
    R processQuery(S source) throws QueryProcessingException;
}

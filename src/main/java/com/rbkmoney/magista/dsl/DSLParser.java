package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface DSLParser<T> {
    Query parse(T source) throws Exception;
}

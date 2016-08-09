package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface FunctionQuery extends Query {

    Class getResultElementType();

    String getName();
}

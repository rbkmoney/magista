package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.DSLInvalidException;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public interface DSLInstanceValidator<T extends DSLInstance> {
    void validate(T instance) throws DSLInvalidException;

}

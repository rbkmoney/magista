package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstanceValidator;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class NopDSLInstanceValidator implements DSLInstanceValidator {
    public static final NopDSLInstanceValidator INSTANCE = new NopDSLInstanceValidator();

    @Override
    public void validate(DSLInstance instance) throws DSLInvalidException {

    }
}

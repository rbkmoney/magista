package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.def.ParameterDef;

/**
 * Created by vpankrashkin on 18.04.17.
 */
public class ValidationUtils {
    public static void validateParamValueNotNull(ParameterDef def, ParameterInstance param) throws DSLInvalidException {
        if (param == null) {
            return;
        }
        if (param.getValue() != null) {
            if (!(param.getValue() instanceof ValueInstance) || ((ValueInstance)param.getValue()).getValue() != null) {
                    return;
            }
        }
        checkNotNull(def, null);
    }

    public static void checkNotNull(DSLDef def, Object obj) {
        if (obj == null) {
            throw genValidationError(def, "Value cannot be null");
        }
    }

    public static void checkTrue(boolean condition, DSLDef def, String message) {
        if (!condition) {
            throw genValidationError(def, message);
        }
    }


    public static DSLInvalidException genValidationError(DSLDef def, String message) {
        return new DSLInvalidException(String.format("%s: %s", def.getClass().getSimpleName(), message));
    }

}

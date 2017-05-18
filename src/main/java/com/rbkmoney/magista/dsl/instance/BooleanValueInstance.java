package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.BooleanValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class BooleanValueInstance extends ValueInstance<BooleanValueDef, Boolean> {
    public BooleanValueInstance() {
        this(null);
    }

    public BooleanValueInstance(Boolean value) {
        super(BooleanValueDef.INSTANCE, value);
    }
}

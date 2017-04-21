package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.BooleanValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class BooleanValueInstance extends ValueInstance<BooleanValueDef, Boolean> {
    public BooleanValueInstance(BooleanValueDef def) {
        super(def);
    }

    public BooleanValueInstance(BooleanValueDef def, Boolean value) {
        super(def, value);
    }
}

package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.BooleanValueInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class BooleanValueDef extends ValueDef {
    public static final BooleanValueDef INSTANCE = new BooleanValueDef();


    @Override
    public DSLInstance createInstance() {
        return new BooleanValueInstance();
    }
}

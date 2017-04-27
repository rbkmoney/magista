package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.StringValueInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class StringValueDef extends ValueDef{
    public static final StringValueDef INSTANCE = new StringValueDef();

    @Override
    public DSLInstance createInstance() {
        return new StringValueInstance();
    }
}

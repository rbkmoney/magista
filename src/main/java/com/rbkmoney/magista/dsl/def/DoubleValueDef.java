package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DoubleValueInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class DoubleValueDef extends NumberValueDef {
    public static final DoubleValueDef INSTANCE = new DoubleValueDef();

    @Override
    public DSLInstance createInstance() {
        return new DoubleValueInstance();
    }
}

package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.LongValueInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class LongValueDef extends NumberValueDef {
    public static final LongValueDef INSTANCE = new LongValueDef();

    @Override
    public DSLInstance createInstance() {
        return new LongValueInstance();
    }
}

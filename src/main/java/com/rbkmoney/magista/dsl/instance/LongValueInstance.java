package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.LongValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class LongValueInstance extends NumberValueInstance<LongValueDef, Long> {
    public LongValueInstance() {
        this(null);
    }

    public LongValueInstance(Long value) {
        super(LongValueDef.INSTANCE, value);
    }
}

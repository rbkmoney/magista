package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.StringValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class StringValueInstance extends ValueInstance<StringValueDef, String> {
    public StringValueInstance() {
        this(null);
    }

    public StringValueInstance(String value) {
        super(StringValueDef.INSTANCE, value);
    }
}

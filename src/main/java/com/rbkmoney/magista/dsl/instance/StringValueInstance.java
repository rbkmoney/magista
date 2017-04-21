package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.StringValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class StringValueInstance extends ValueInstance<StringValueDef, String> {
    public StringValueInstance(StringValueDef def) {
        super(def);
    }

    public StringValueInstance(StringValueDef def, String value) {
        super(def, value);
    }
}

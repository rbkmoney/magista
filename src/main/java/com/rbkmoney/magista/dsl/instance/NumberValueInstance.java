package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class NumberValueInstance<T extends ValueDef, V extends Number> extends ValueInstance<T, V> {
    public NumberValueInstance(T def) {
        super(def);
    }

    public NumberValueInstance(T def, V value) {
        super(def, value);
    }
}

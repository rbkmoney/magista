package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ValueInstance<T extends ValueDef, V> extends UnkeyedInstance<T, DSLInstance> {
    private V value;
    public ValueInstance(T def) {
        super(def);
    }

    public ValueInstance(T def, V value) {
        super(def);
        this.value = value;
    }


    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}

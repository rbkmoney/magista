package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ArrayDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ArrayInstance<V extends DSLInstance> extends UnkeyedInstance<ArrayDef, V> {
    public ArrayInstance(ArrayDef def) {
        super(def);
    }
}

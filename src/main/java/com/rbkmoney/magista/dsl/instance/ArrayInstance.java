package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ArrayDef;
import com.rbkmoney.magista.dsl.def.DSLDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ArrayInstance<V extends DSLInstance> extends UnkeyedInstance<ArrayDef, V> {
    public ArrayInstance(ArrayDef def) {
        super(def);
    }

    @Override
    public void setChild(DSLDef def, DSLInstance instance) {
        checkChildDef(def);
        getChildren().add((V) instance);
    }
}

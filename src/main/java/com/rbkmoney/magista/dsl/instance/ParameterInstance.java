package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ParameterDSLDef;
import com.rbkmoney.magista.dsl.def.ParameterDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ParameterInstance<T extends ParameterDSLDef, V extends DSLInstance> extends KeyedInstance<T> {

    public ParameterInstance(T def) {
        super(def);
    }

    public String getName() {
        return getDef().getName();
    }

    public V getValue() {
        return (V) getChild(ParameterDef.VALUE_KEY);
    }

    public void setValue(V value) {
        putChild(ParameterDef.VALUE_KEY, value);
    }
}

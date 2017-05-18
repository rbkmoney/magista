package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.*;

/**
 * Created by vpankrashkin on 07.04.17.
 */
public abstract class UnkeyedInstance<T extends DSLDef, V extends DSLInstance> extends Instance<T> {
    private final List<V> children = new ArrayList<>();

    public UnkeyedInstance(T def) {
        super(def);
    }

    @Override
    public List<V> getChildren() {
        return children;
    }

}

package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpankrashkin on 07.04.17.
 */
public abstract class Instance<T extends DSLDef> implements DSLInstance<T> {
    private final T def;
    private DSLInstance parent;

    public Instance(T def) {
        this.def = def;
    }

    public T getDef() {
        return def;
    }

    @Override
    public DSLInstance getParent() {
        return parent;
    }

    public void setParent(DSLInstance parent) {
        this.parent = parent;
    }

    protected boolean isChildDef(DSLDef def) {
        return getDef().getChildDefs().contains(def);
    }

    protected void checkChildDef(DSLDef def) {
        if (!isChildDef(def)) {
            throw new IllegalArgumentException("Expected def in: "+ getDef().getChildDefs() + " but referred: " + def);
        }
    }

}

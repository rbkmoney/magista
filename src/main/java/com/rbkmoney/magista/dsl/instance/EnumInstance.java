package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.def.EnumDef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpankrashkin on 07.04.17.
 */
public abstract class EnumInstance<T extends EnumDef> extends Instance<T> {
    private List<DSLInstance> value = new ArrayList<>();

    public EnumInstance(T def) {
        super(def);
    }

    public DSLInstance getEnumValue() {
        return value.isEmpty() ? null : value.get(0);
    }

    public void setEnumValue(DSLInstance value) {
        for (DSLDef def: getDef().getChildDefs()) {
            if (def.isAssignable(value)) {
                this.value.set(0, value);
                return;
            }
        }
        throw new IllegalStateException("No matching def found for instance value");
    }

    @Override
    public Collection<? extends DSLInstance> getChildren() {
        return Collections.unmodifiableList(value);
    }
}

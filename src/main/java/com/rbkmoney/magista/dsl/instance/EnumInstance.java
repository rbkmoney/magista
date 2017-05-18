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
public class EnumInstance<T extends EnumDef> extends Instance<T> {
    private List<DSLInstance> value = new ArrayList<>();

    public EnumInstance(T def) {
        super(def);
    }

    public DSLInstance getEnumValue() {
        return value.isEmpty() ? null : value.get(0);
    }

    public void setEnumValue(DSLDef def, DSLInstance value) {
        setChild(def, value);
    }

    @Override
    public void setChild(DSLDef def, DSLInstance value) {
        checkChildDef(def);
        this.value.set(0, value);
    }

    @Override
    public Collection<? extends DSLInstance> getChildren() {
        return Collections.unmodifiableList(value);
    }
}

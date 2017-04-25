package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;

import java.util.List;
import java.util.Objects;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public interface DSLDef {
    List<DSLDef> getChildDefs();
    default boolean isAssignable(DSLInstance instance) {
        if (instance != null) {
            return  Objects.equals(instance.getDef(), this);
        }
        return true;
    }


}

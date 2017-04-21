package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public interface DSLInstance<T extends DSLDef> {
    T getDef();
    DSLInstance getParent();
    Collection<? extends DSLInstance> getChildren();
}

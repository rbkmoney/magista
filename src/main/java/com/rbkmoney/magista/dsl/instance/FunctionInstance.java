package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.def.FunctionDSLDef;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 07.04.17.
 */
public abstract class FunctionInstance<T extends DSLDef> extends KeyedInstance<T> {
    public FunctionInstance(T def) {
        super(def);
    }
    public Map<String, FunctionInstance> getChildFunctions() {
        return getChildren().stream().filter(inst -> inst.getDef() instanceof FunctionDSLDef).collect(Collectors.toMap(inst -> ((FunctionDSLDef)inst.getDef()).getName(), inst -> (FunctionInstance) inst));
    }

}

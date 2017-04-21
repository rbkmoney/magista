package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.ParameterDSLDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class VarParameterInstance<T extends ParameterDSLDef, V extends DSLInstance> extends ParameterInstance<T, V> {
    private String varName;

    public VarParameterInstance(T def) {
        super(def);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

}

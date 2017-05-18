package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.VarParameterInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class VarParameterDef extends ParameterDef implements VarNamedDSLDef {
    public VarParameterDef(DSLDef valueDef) {
        super(valueDef, null);
    }

    @Override
    public DSLInstance createInstance() {
        return new VarParameterInstance(this);
    }
}

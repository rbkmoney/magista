package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.ParameterInstance;

import java.util.Arrays;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ParameterDef extends NamedDef implements ParameterDSLDef {
    public static final String VALUE_KEY = "PARAM_VALUE";

    public ParameterDef(DSLDef valueDef, String name) {
        super(Arrays.asList(valueDef), name);
    }

    public DSLDef getValueDef() {
        return getChildDefs().get(0);
    }

    @Override
    public DSLInstance createInstance() {
        return new ParameterInstance(this);
    }
}

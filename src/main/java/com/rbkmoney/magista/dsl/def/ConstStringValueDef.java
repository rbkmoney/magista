package com.rbkmoney.magista.dsl.def;

import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.StringValueInstance;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class ConstStringValueDef extends StringValueDef implements ConstantValueDSLDef<String> {
    private final String value;

    public ConstStringValueDef(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public DSLInstance createInstance() {
        return new StringValueInstance();
    }
}

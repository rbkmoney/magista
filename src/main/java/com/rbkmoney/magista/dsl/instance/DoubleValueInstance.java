package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DoubleValueDef;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public class DoubleValueInstance extends NumberValueInstance<DoubleValueDef, Double> {
    public DoubleValueInstance(DoubleValueDef def) {
        super(def);
    }
    public DoubleValueInstance(DoubleValueDef def, Double value) {
        super(def, value);
    }
}

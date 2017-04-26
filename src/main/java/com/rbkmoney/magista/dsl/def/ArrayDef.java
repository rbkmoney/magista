package com.rbkmoney.magista.dsl.def;

import java.util.Arrays;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public abstract class ArrayDef extends Def {

    public ArrayDef(DSLDef itemsDef) {
        super(Arrays.asList(itemsDef));
    }

    public DSLDef getItemsDef() {
        return getChildDefs().get(0);
    }
}

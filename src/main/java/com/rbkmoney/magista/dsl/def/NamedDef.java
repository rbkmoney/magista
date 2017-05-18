package com.rbkmoney.magista.dsl.def;

import java.util.List;

/**
 * Created by vpankrashkin on 07.04.17 with pain and desperate void inside.
 */
public abstract class NamedDef extends Def implements NamedDSLDef {
    private final String name;

    protected NamedDef(List<? extends DSLDef> nestedDSLList, String name) {
        super(nestedDSLList);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

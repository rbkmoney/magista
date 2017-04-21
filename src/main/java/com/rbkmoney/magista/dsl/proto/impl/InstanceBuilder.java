package com.rbkmoney.magista.dsl.proto.impl;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstanceBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpankrashkin on 21.04.17.
 */
public class InstanceBuilder implements DSLInstanceBuilder<> {
    private Map<DSLDef, DSLInstanceBuilder> builders = new HashMap<>();


    @Override
    public DSLInstance build(Object scr, DSLDef def, DSLInstance parentInstance) throws DSLBuildException {
        return null;
    }
}

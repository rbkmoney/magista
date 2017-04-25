package com.rbkmoney.magista.dsl.proto.impl;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstanceBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by vpankrashkin on 21.04.17.
 */
public class InstanceBuilder<T extends DSLInstance> implements DSLInstanceBuilder<T> {
    private Function<DSLDef, T> creator;

    public InstanceBuilder(Function<DSLDef, T> creator) {
        this.creator = creator;
    }

    @Override
    public T build(Object src, DSLDef def, DSLInstance parentInstance, Map<DSLDef, DSLInstanceBuilder> builders) throws DSLBuildException {
        T instance = createInstance(def);

        List<DSLDef> childDefs = def.getChildDefs();
        for (int i = 0; i < childDefs.size(); ++i) {
            DSLDef childDef = childDefs.get(i);
            DSLInstanceBuilder builder = builders.get(childDef);
            if (builder == null) {
                throw new DSLBuildException("Not found builder for def: " + childDef);
            }
            DSLInstance value = builder.build(src, childDef, parentInstance, builders);
            instance.setChild(childDef, value);
        }
        return instance;
    }

    protected T createInstance(DSLDef def) {
        return creator.apply(def);
    }
}

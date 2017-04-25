package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.Map;

/**
 * Created by vpankrashkin on 12.04.17.
 */
public interface DSLInstanceBuilder<T extends DSLInstance> {
     /** @return built definition if necessary information was found; null - if necessary information wasn't found.
            * @throws DSLBuildException if necessary information to start building dsl instance was found but any problem occurred during build.
     * */
    T build(Object src, DSLDef def, DSLInstance parentInstance, Map<DSLDef, DSLInstanceBuilder> builders) throws DSLBuildException;
}

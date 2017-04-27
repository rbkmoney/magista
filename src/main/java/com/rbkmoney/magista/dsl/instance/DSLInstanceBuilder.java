package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.Map;

/**
 * Created by vpankrashkin on 12.04.17.
 */
public interface DSLInstanceBuilder<T extends DSLInstance> {
     /**
      * @param src - implementation specific value of defTree
      * @param defTree - definition of object that must be built
      * @param parentInstance - parent instance, null for root
      * @return built definition if necessary information was found; null - if necessary information wasn't found.
            * @throws DSLBuildException if necessary information to start building dsl instance was found but any problem occurred during buildBase.
     * */
    T build(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;
}

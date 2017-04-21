package com.rbkmoney.magista.dsl.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by vpankrashkin on 07.04.17 with pain and desperate void inside.
 */
public abstract class Def implements DSLDef {
    private final List<DSLDef> childDefList;

    protected Def(List<? extends DSLDef> childDefList) {
        Objects.nonNull(childDefList);
        this.childDefList = Collections.unmodifiableList(new ArrayList<DSLDef>(childDefList));
    }


/*    public T build(Object scr, DSLInstance parentInstance) throws DSLBuildException {
        if (builder == null) {
            throw new DSLBuildException("Not configured to build instance");
        }
        return builder.build(scr, this, parentInstance);
    }


    public void validate(T instance) throws DSLInvalidException {
        if (validator != null) {
            validator.validate(instance);
        }
        for (DSLInstance nestedInstance: instance.getChildrenMap().values()) {
            nestedInstance.getDef().validate(nestedInstance);
        }
    }*/

    @Override
    public List<DSLDef> getChildDefs() {
        return childDefList;
    }

}

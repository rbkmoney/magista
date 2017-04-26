package com.rbkmoney.magista.dsl.proto.impl;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vpankrashkin on 21.04.17.
 */
public abstract class InstanceBuilder<T extends DSLInstance> implements DSLInstanceBuilder<T> {
    private Map<DSLDef, DSLInstanceBuilder> builders = new HashMap<>();

    public InstanceBuilder(Map<DSLDef, DSLInstanceBuilder> builders) {
        this.builders.putAll(builders);
    }

    @Override
    public T build(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        if (src == null) {
            return null;
        }
        DSLInstanceBuilder builder = builders.get(defTree.getNode());
        if (builder != null) {
            return (T) builder.build(src, defTree, parentInstance);
        } else {
            return  (T) fillInstance(src, defTree, buildBase(src, defTree, parentInstance));
        }
    }

    protected DSLInstance fillInstance(Object src, DefTree defTree, DSLInstance instance) {
        List<DSLDef> childDefs = defTree.getNode().getChildDefs();
        for (int i = 0; i < childDefs.size(); ++i) {
            DSLDef childDef = childDefs.get(i);
            //build list
            DSLInstance value = build(walkTree(src, defTree), , instance);
            instance.setChild(childDef, value);
            defTree.remNode();
        }
        return instance;
    }

    /**
     * @param  src parent, root or any other object, depends on implementation
     * @return src object representation of last defTree node or any other object, allowing to get this value
     * */
    protected Object walkTree(Object src, DefTree defTree) {
        return src;
    }

    private DSLInstance buildBase(Object parentSrc, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = defTree.getNode();

        if (def instanceof FunctionDef) {
            return buildBaseFunc(parentSrc, defTree, parentInstance);
        } else if (def instanceof ArrayDef) {
            return buildBaseArray(parentSrc, defTree, parentInstance);
        } else if (def instanceof EnumDef) {
            return buildBaseEnum(parentSrc, defTree, parentInstance);
        } else if (def instanceof ParameterDef) {
            return buildBaseParam(parentSrc, defTree, parentInstance);
        } else if (def instanceof ValueDef) {
            return buildBaseVal(parentSrc, defTree, parentInstance);
        } else {
            return buildUndefined(parentSrc, defTree, parentInstance);
        }
    }

    protected FunctionInstance buildBaseFunc(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        return (FunctionInstance) defTree.getNode().createInstance();
    }

    //add smth to tell that instance is already filled
    protected ArrayInstance buildBaseArray(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {

    }

    protected EnumInstance buildBaseEnum(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {

    }

    protected ValueInstance buildBaseVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = defTree.getNode();
        if (def instanceof NumberValueDef) {
            return buildBaseNumVal(src, defTree, parentInstance);
        } else if (def instanceof StringValueDef) {
            return buildBaseStrVal(src, defTree, parentInstance);
        } else if (def instanceof BooleanValueDef) {
            return buildBaseBoolVal(src, defTree, parentInstance);
        } else {
            return buildUndefinedVal(src, defTree, parentInstance);
        }
    }

    protected NumberValueInstance buildBaseNumVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = defTree.getNode();
        if (def instanceof LongValueDef) {
            return buildBaseLongVal(src, defTree, parentInstance);
        } else if (def instanceof DoubleValueDef) {
            return buildBaseDoubleVal(src, defTree, parentInstance);
        } else {
            return buildUndefinedNumVal(src, defTree, parentInstance);
        }
    }

    abstract protected LongValueInstance buildBaseLongVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;
    abstract protected LongValueInstance buildBaseDoubleVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;
    abstract protected BooleanValueInstance buildBaseBoolVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    protected NumberValueInstance buildUndefinedNumVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown number def: "+ defTree.getNode());
    }

    protected ValueInstance buildUndefinedVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown value def: "+ defTree.getNode());
    }

    abstract protected StringValueInstance buildBaseStrVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;


    protected ParameterInstance buildBaseParam(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = defTree.getNode();
        if (def instanceof VarParameterDef) {
            return buildBaseVarParam(src, defTree, parentInstance);
        } else {
            return buildUndefinedParam(src, defTree, parentInstance);
        }
    }

    abstract protected VarParameterInstance buildBaseVarParam(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected ParameterInstance buildUndefinedParam(Object src, DefTree def, DSLInstance parentInstance) throws DSLBuildException;
    abstract protected ParameterInstance buildUndefined(Object src, DefTree def, DSLInstance parentInstance) throws DSLBuildException;

}

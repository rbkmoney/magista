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
            return (T) fillInstance(src, defTree, buildBase(src, defTree, parentInstance));
        }
    }

    private DSLInstance fillInstance(Object src, DefTree defTree, DSLInstance instance) {
        if (defTree.getNode() != instance.getDef()) {//if tree contains child defs, consider they're already processed
            while (defTree.getNode() != null && defTree.getNode() != instance.getDef()) {
                defTree.remNode();
            }
            return instance;
        }

        if (defTree.getNode() instanceof ArrayDef){
            return fillArrayInstance(src, defTree, (ArrayInstance) instance);
        } else {
            List<DSLDef> childDefs = defTree.getNode().getChildDefs();
            for (int i = 0; i < childDefs.size(); ++i) {
                DSLDef childDef = childDefs.get(i);
                DSLInstance value = build(walkTree(src, defTree.addNode(childDef)), defTree, instance);
                instance.setChild(childDef, value);
                defTree.remNode();
            }
            return instance;
        }
    }


    private ArrayInstance fillArrayInstance(Object src, DefTree defTree, ArrayInstance instance) {
        ArrayDef def = (ArrayDef) defTree.getNode();
        DSLDef itemsDef = ((ArrayDef)defTree.getNode()).getItemsDef();
        int i;
        for (i = 0; hasMoreElements(src, def, defTree, i); ++i) {
            DSLInstance value = build(walkTree(src, defTree.addNode(itemsDef)), defTree, instance);
            instance.setChild(itemsDef, value);
        }
        while (i-- > 0) {
            defTree.remNode();
        }
        return instance;
    }

    abstract protected boolean hasMoreElements(Object src, ArrayDef def, DefTree defTree, int processedCount);

    /**
     * @param src parent, root or any other object, depends on implementation. By default def tree navigation is relied on builder implementation.
     *            For navigating in array, all sequentially read defs're added as tree nodes, so same defs count to nearest array def up in tree is number or reading element (1 based)
     * @return src object representation of last defTree node or any other object, allowing to get this value
     */
    protected Object walkTree(Object src, DefTree defTree) {
        return src;
    }

    private DSLInstance buildBase(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        if (src == null) {
            return null;
        }

        DSLDef def = defTree.getNode();

        if (def instanceof FunctionDef) {
            return buildBaseFunc(src, defTree, parentInstance);
        } else if (def instanceof ArrayDef) {
            return buildBaseArray(src, defTree, parentInstance);
        } else if (def instanceof EnumDef) {
            return buildBaseEnum(src, defTree, parentInstance);
        } else if (def instanceof ParameterDef) {
            return buildBaseParam(src, defTree, parentInstance);
        } else if (def instanceof ValueDef) {
            return buildBaseVal(src, defTree, parentInstance);
        } else {
            return buildUndefined(src, defTree, parentInstance);
        }
    }

    protected FunctionInstance buildBaseFunc(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        return (FunctionInstance) defTree.getNode().createInstance();
    }

    protected EnumInstance buildBaseEnum(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        return (EnumInstance) defTree.getNode().createInstance();
    }

    protected abstract ArrayInstance buildBaseArray(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

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


    abstract protected VarParameterInstance buildBaseVarParam(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected LongValueInstance buildBaseLongVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected LongValueInstance buildBaseDoubleVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected BooleanValueInstance buildBaseBoolVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected StringValueInstance buildBaseStrVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException;

    protected NumberValueInstance buildUndefinedNumVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown number def: " + defTree.getNode());
    }


    protected ValueInstance buildUndefinedVal(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown value def: " + defTree.getNode());
    }

    protected ParameterInstance buildBaseParam(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = defTree.getNode();
        if (def instanceof VarParameterDef) {
            return buildBaseVarParam(src, defTree, parentInstance);
        } else {
            return buildUndefinedParam(src, defTree, parentInstance);
        }
    }

    protected ParameterInstance buildUndefinedParam(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown param def: " + defTree.getNode());
    }

    protected ParameterInstance buildUndefined(Object src, DefTree defTree, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown def: " + defTree.getNode());
    }

}

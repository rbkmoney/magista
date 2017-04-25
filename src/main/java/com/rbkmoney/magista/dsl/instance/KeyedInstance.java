package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.def.ParameterDSLDef;
import com.rbkmoney.magista.dsl.def.ParameterDef;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpankrashkin on 07.04.17.
 */
public abstract class KeyedInstance<T extends DSLDef> extends Instance<T> {
    private final Map<Object, DSLInstance> children = new HashMap<>();

    public KeyedInstance(T def) {
        super(def);
    }

    @Override
    public void setChild(DSLDef def, DSLInstance instance) {
        checkChildDef(def);
        children.put(def, instance);
    }

    @Override
    public Collection<DSLInstance> getChildren() {
        return children.values();
    }

    public Map<Object, DSLInstance> getChildrenMap() {
        return children;
    }

    public DSLInstance getChild(DSLDef def) {
        return children.get(def);
    }

    public ParameterInstance getOrCreateParameter(ParameterDSLDef def) {
        ParameterInstance param = getParameter(def);
        if (param == null) {
            param = new ParameterInstance(def);
            setParameter(def, param);
        }
        return param;
    }

    public ParameterInstance getParameter(ParameterDSLDef def) {
        Object value = getChild(def);
        if (value instanceof ParameterInstance) {
            return (ParameterInstance) value;
        } else {
            return null;
        }
    }

    public <V> V getParameterValue(ParameterDSLDef def, Class<V> expectedType) {
        ParameterInstance param = getParameter(def);
        if (param != null && param.getValue() instanceof ValueInstance) {
            Object value =  ((ValueInstance) param.getValue()).getValue();
            if (value == null || expectedType.isAssignableFrom(value.getClass())) {
                return (V) value;
            }
        }
        return null;
    }

    public void setParameter(ParameterDSLDef def, ParameterInstance value) {
        setChild(def, value);
    }
}

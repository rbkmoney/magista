package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.def.ParameterDSLDef;

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
    public Collection<DSLInstance> getChildren() {
        return children.values();
    }

    public Map<Object, DSLInstance> getChildrenMap() {
        return children;
    }

    public DSLInstance getChild(Object key) {
        return children.get(key);
    }

    public void putChild(Object key, DSLInstance value) {
        children.put(key, value);
    }

    public ParameterInstance getOrCreateParameter(ParameterDSLDef def) {
        return getOrCreateParameter(def.getName(), def);
    }

    public ParameterInstance getOrCreateParameter(Object key, ParameterDSLDef def) {
        ParameterInstance param = getParameter(key);
        if (param == null) {
            param = new ParameterInstance(def);
            setParameter(key, param);
        }
        return param;
    }

    public ParameterInstance getParameter(ParameterDSLDef def) {
        return getParameter(def.getName());
    }

    public ParameterInstance getParameter(Object key) {
        Object value = getChild(key);
        if (value instanceof ParameterInstance) {
            return (ParameterInstance) value;
        } else {
            return null;
        }
    }

    public <V1> V1 getParameterValue(ParameterDSLDef def, Class<V1> expectedType) {
        return getParameterValue(def.getName(), expectedType);
    }

    public <V> V getParameterValue(Object key, Class<V> expectedType) {
        ParameterInstance param = getParameter(key);
        if (param != null && param.getValue() instanceof ValueInstance) {
            Object value =  ((ValueInstance) param.getValue()).getValue();
            if (value == null || expectedType.isAssignableFrom(value.getClass())) {
                return (V) value;
            }
        }
        return null;
    }

    public void setParameter(ParameterInstance value) {
        setParameter(value.getName(), value);
    }

    public void setParameter(Object key, ParameterInstance value) {
        putChild(key, value);
    }
}

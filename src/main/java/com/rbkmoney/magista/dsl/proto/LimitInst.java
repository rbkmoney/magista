package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.instance.KeyedInstance;
import com.rbkmoney.magista.dsl.instance.UnkeyedInstance;
import com.rbkmoney.magista.dsl.instance.LongValueInstance;
import com.rbkmoney.magista.dsl.instance.ParameterInstance;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class LimitInst extends KeyedInstance {

    public LimitInst() {
        super(LimitDef.INSTANCE);
    }

    public Size getSize() {
        return (Size) getChild(LimitDef.SizeDef.NAME);
    }

    public void setSize(Size size) {
        putChild(LimitDef.SizeDef.NAME, size);
    }

    public From getFrom() {
        return (From) getChild(LimitDef.FromDef.NAME);
    }

    public void setFrom(From from) {
        putChild(LimitDef.FromDef.NAME, from);
    }

    public static class Size extends ParameterInstance<LimitDef.SizeDef, LongValueInstance> {
        public Size() {
            this(null);
        }

        public Size(Long value) {
            super(LimitDef.SizeDef.INSTANCE);
            setValue(new LongValueInstance(value));
        }
    }

    public static class From extends ParameterInstance<LimitDef.FromDef, LongValueInstance> {
        public From() {
            this(null);
        }

        public From(Long value) {
            super(LimitDef.FromDef.INSTANCE);
            setValue(new LongValueInstance(value));
        }

    }
}

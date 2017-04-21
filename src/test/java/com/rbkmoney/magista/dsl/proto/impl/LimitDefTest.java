package com.rbkmoney.magista.dsl.proto.impl;


import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.def.DSLDef;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstanceBuilder;
import com.rbkmoney.magista.dsl.proto.LimitDef;
import com.rbkmoney.magista.dsl.proto.LimitInst;
import org.junit.Test;

import java.util.function.Function;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class LimitDefTest {
    @Test(expected = DSLInvalidException.class)
    public void testValidateNoParams() {
        LimitDef def = new LimitDef(genBuilder(Function.identity()));
        buildAndValidate(def);
    }

    @Test(expected = DSLInvalidException.class)
    public void testValidateNullSizeParam() {
        DSLInstanceBuilder noParamsBuilder = new DSLInstanceBuilder() {
            @Override
            public DSLInstance build(Object src, DSLDef def, DSLInstance parent) throws DSLBuildException {
                if (def instanceof LimitDef) {
                    LimitInst limit = new LimitInst((LimitDef) def);
                    limit.setSize((LimitInst.Size) build(src, LimitDef.SizeDef.INSTANCE, parent));
                    return limit;
                } else if (def instanceof LimitDef.SizeDef) {
                    return new LimitInst.Size((LimitDef.SizeDef) def);
                }
                return null;
            }
        };
        LimitDef def = new LimitDef(noParamsBuilder);
        LimitInst limit = def.build(null, null);
        def.validate(limit);
    }

    @Test(expected = DSLInvalidException.class)
    public void testValidateZeroSizeParam() {
        LimitDef def = new LimitDef(genBuilder(limit -> {
            limit.setSize(new LimitInst.Size(0L));
            return limit;
        }));
        buildAndValidate(def);
    }

    @Test(expected = DSLInvalidException.class)
    public void testValidateNegativeOffsetParam() {
        LimitDef def = new LimitDef(genBuilder(limit -> {
            limit.setFrom(new LimitInst.From(-1L));
            return limit;
        }));
        buildAndValidate(def);
    }

    private LimitInst buildAndValidate(LimitDef def) {
        LimitInst limit = def.build(null, null);
        def.validate(limit);
        return limit;
    }

    private DSLInstanceBuilder<LimitInst> genBuilder(Function<LimitInst, LimitInst> supp) {
        return  (src, def, parent) -> {
            if (def instanceof LimitDef) {
                LimitInst limit = new LimitInst((LimitDef) def);
                return supp.apply(limit);
            }
            return null;
        };
    }

}

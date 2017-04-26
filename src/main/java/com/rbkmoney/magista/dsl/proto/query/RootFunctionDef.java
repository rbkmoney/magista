package com.rbkmoney.magista.dsl.proto.query;

import com.rbkmoney.magista.dsl.def.FunctionDSLDef;
import com.rbkmoney.magista.dsl.def.FunctionDef;
import com.rbkmoney.magista.dsl.instance.DSLInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by vpankrashkin on 14.04.17.
 */
public class RootFunctionDef extends FunctionDef {
    public static final String NAME = "root";

    public RootFunctionDef(List<FunctionDSLDef> functionDefs) {
        super(functionDefs, Collections.EMPTY_LIST, Collections.EMPTY_LIST, NAME);
    }

    @Override
    public DSLInstance createInstance() {
        return new RootFunctionInst(this);
    }
}

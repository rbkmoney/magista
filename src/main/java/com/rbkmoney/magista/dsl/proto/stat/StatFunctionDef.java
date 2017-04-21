package com.rbkmoney.magista.dsl.proto.stat;

import com.rbkmoney.magista.dsl.def.FunctionDef;
import com.rbkmoney.magista.dsl.def.FunctionDSLDef;

import java.util.Collections;
import java.util.List;

/**
 * Created by vpankrashkin on 14.04.17.
 */
public class StatFunctionDef extends FunctionDef {
    public static final String NAME = "stat";

    public StatFunctionDef(List<FunctionDSLDef> functionDefs) {
        super(functionDefs, Collections.EMPTY_LIST, Collections.EMPTY_LIST, NAME);
    }
}

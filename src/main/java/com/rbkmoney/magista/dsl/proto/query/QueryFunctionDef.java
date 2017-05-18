package com.rbkmoney.magista.dsl.proto.query;

import com.rbkmoney.magista.dsl.def.FunctionDef;
import com.rbkmoney.magista.dsl.def.FunctionDSLDef;
import com.rbkmoney.magista.dsl.instance.DSLInstance;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpankrashkin on 14.04.17.
 */
public class QueryFunctionDef extends FunctionDef {
    public static final String NAME = "query";
    public static final QueryFunctionDef INSTANCE = new QueryFunctionDef();

    public QueryFunctionDef() {
        this(Arrays.asList(PaymentsQueryDef.INSTANCE));
    }

    public QueryFunctionDef(List<FunctionDSLDef> functionDefs) {
        super(functionDefs, Collections.EMPTY_LIST, Collections.EMPTY_LIST, NAME);
    }

    @Override
    public DSLInstance createInstance() {
        return new QueryFunctionInst(this);
    }
}

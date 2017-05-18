package com.rbkmoney.magista.dsl.proto.query;

import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.proto.LimitDef;
import com.rbkmoney.magista.dsl.proto.SortDef;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpankrashkin on 14.04.17.
 */
public class PaymentsQueryDef extends FunctionDef {
    public static final String NAME = "payments";
    public static final ParameterDSLDef PAYMENT_ID_DEF =  new ParameterDef(LongValueDef.INSTANCE, "payment_id");
    public static final PaymentsQueryDef INSTANCE = new PaymentsQueryDef();

    public PaymentsQueryDef() {
        super(genNestedDefs(), genInputParameterDefs(), Collections.EMPTY_LIST,  NAME);
    }

    protected static List<NamedDSLDef> genNestedDefs() {
        return Arrays.asList(SortDef.INSTANCE, LimitDef.INSTANCE);
    }

    protected static List<ParameterDSLDef> genInputParameterDefs() {
        return Arrays.asList(PAYMENT_ID_DEF
        );
    }

    @Override
    public DSLInstance createInstance() {
        return new PaymentsQueryInst();
    }
}

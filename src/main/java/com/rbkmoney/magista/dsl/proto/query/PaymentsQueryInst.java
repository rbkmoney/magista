package com.rbkmoney.magista.dsl.proto.query;

import com.rbkmoney.magista.dsl.instance.FunctionInstance;
import com.rbkmoney.magista.dsl.instance.LongValueInstance;
import com.rbkmoney.magista.dsl.instance.ParameterInstance;
import com.rbkmoney.magista.dsl.proto.LimitDef;
import com.rbkmoney.magista.dsl.proto.LimitInst;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class PaymentsQueryInst extends FunctionInstance {
    public PaymentsQueryInst() {
        super(PaymentsQueryDef.INSTANCE);
    }

    public LimitInst getLimit() {
        return (LimitInst) getChild(LimitDef.NAME);
    }

    public void setLimit(LimitInst limit) {
        putChild(LimitDef.NAME, limit);
    }

    public Long getPaymentIdParameter() {
        return (Long) getParameterValue(PaymentsQueryDef.PAYMENT_ID_DEF, Long.class);//TODO wtf?
    }

    public void setPaymentIdParameter(Long value) {
        ParameterInstance param = getOrCreateParameter(PaymentsQueryDef.PAYMENT_ID_DEF);
        param.setValue(new LongValueInstance(value));
    }
}

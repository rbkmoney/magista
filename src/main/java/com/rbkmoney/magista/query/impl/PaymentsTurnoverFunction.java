package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryResult;

import java.util.Map;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsTurnoverFunction extends StatBaseFunction {


    public static final String FUNC_NAME = "payments_turnover";

    public PaymentsTurnoverFunction(Map<String, Object> params) {
        super(params, Map.class, FUNC_NAME);
    }

    @Override
    public QueryResult execute(QueryContext context) {
        return null;
    }

}

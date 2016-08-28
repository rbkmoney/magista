package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsTurnoverStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "payments_turnover";

    public PaymentsTurnoverStatFunction(QueryParameters params, Query parentQuery) {
        super(params, parentQuery, FUNC_NAME);
    }

    public static class PaymentsTurnoverStatParser extends StatBaseParser {

        public PaymentsTurnoverStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

}

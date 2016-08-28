package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsConversionStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "payments_conversion_stat";

    public PaymentsConversionStatFunction(QueryParameters params, Query parentQuery) {
        super(params, parentQuery, FUNC_NAME);
    }

    public static class PaymentsConversionStatParser extends StatBaseParser {

        public PaymentsConversionStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

}

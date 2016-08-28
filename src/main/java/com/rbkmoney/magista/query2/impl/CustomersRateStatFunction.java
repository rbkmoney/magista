package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class CustomersRateStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "customers_rate_stat";

    public CustomersRateStatFunction(QueryParameters params, Query parentQuery) {
        super(params, parentQuery, FUNC_NAME);
    }

    public static class CustomersRateStatParser extends StatBaseParser {

        public CustomersRateStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

}

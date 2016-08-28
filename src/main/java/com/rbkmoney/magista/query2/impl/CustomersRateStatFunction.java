package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class CustomersRateStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "customers_rate_stat";

    public CustomersRateStatFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    public static class CustomersRateStatParser extends StatBaseParser {

        public CustomersRateStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class CustomersRateStatBuilder extends StatBaseBuilder {

        @Override
        protected Query createQuery(QueryPart queryPart) {
            return new CustomersRateStatFunction(queryPart.getDescriptor(), queryPart.getParameters());
        }

        @Override
        protected Object getDescriptor(List<QueryPart> queryParts) {
            return CustomersRateStatParser.getMainDescriptor();
        }
    }

}

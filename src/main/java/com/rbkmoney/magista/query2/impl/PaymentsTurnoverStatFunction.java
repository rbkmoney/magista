package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsTurnoverStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "payments_turnover";

    public PaymentsTurnoverStatFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    public static class PaymentsTurnoverStatParser extends StatBaseParser {

        public PaymentsTurnoverStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class PaymentsTurnoverStatBuilder extends StatBaseBuilder {

        @Override
        protected Query createQuery(QueryPart queryPart) {
            return new PaymentsTurnoverStatFunction(queryPart.getDescriptor(), queryPart.getParameters());
        }

        @Override
        protected Object getDescriptor(List<QueryPart> queryParts) {
            return PaymentsTurnoverStatParser.getMainDescriptor();
        }
    }

}

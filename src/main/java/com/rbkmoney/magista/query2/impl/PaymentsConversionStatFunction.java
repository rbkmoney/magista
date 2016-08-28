package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsConversionStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "payments_conversion_stat";

    public PaymentsConversionStatFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    public static class PaymentsConversionStatParser extends StatBaseParser {

        public PaymentsConversionStatParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class PaymentsConversionStatBuilder extends StatBaseBuilder {

        @Override
        protected Query createQuery(QueryPart queryPart) {
            return new PaymentsConversionStatFunction(queryPart.getDescriptor(), queryPart.getParameters());
        }

        @Override
        protected Object getDescriptor(List<QueryPart> queryParts) {
            return PaymentsConversionStatParser.getMainDescriptor();
        }
    }

}

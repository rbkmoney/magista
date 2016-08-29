package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.query2.*;
import com.rbkmoney.magista.query2.parser.QueryPart;
import com.rbkmoney.magista.repository.DaoException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsConversionStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "payments_conversion_stat";

    private PaymentsConversionStatFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    @Override
    public QueryResult<Map<String, String>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        try {
            Collection<Map<String, String>> result = getContext(context).getDao().getPaymentsConversionStat(
                    getQueryParameters().getMerchantId(),
                    getQueryParameters().getShopId(),
                    Instant.from(getQueryParameters().getFromTime()),
                    Instant.from(getQueryParameters().getToTime()),
                    getQueryParameters().getSplitInterval()
            );
            return new BaseQueryResult<>(() -> result.stream(), () -> new StatResponse(StatResponseData.records(result.stream().collect(Collectors.toList()))));
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
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

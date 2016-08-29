package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.exception.DaoException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class CustomersRateStatFunction extends StatBaseFunction {

    public static final String FUNC_NAME = "customers_rate_stat";

    private CustomersRateStatFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    @Override
    public QueryResult<Map<String, String>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        try {
            Collection<Map<String, String>> result = getContext(context).getDao().getCustomersRateStat(
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

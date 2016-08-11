package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryExecutionException;
import com.rbkmoney.magista.query.QueryResult;
import com.rbkmoney.magista.repository.DaoException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class PaymentsGeoStatFunction extends StatBaseFunction {


    public static final String FUNC_NAME = "payments_geo_stat";

    public PaymentsGeoStatFunction(Map<String, Object> params) {
        super(params, Map.class, FUNC_NAME);
    }

    private BiFunction<Stream<Map<String, String>>, QueryResult, Supplier<StatResponse>> dataCollectorFunction = (st, qr) -> {
        StatResponseData statResponseData = StatResponseData.records(st.collect(Collectors.toList()));
        StatResponse statResponse = new StatResponse(statResponseData);
        return () -> statResponse;
    };

    @Override
    public QueryResult execute(FunctionQueryContext context) throws QueryExecutionException {
        try {
            Collection<Map<String, String>> result = context.getDao().getPaymentsGeoStat(
                    getMerchantId(),
                    getShopId(),
                    Instant.from(getFromTime()),
                    Instant.from(getToTime()),
                    getSplitInterval()
            );
            return new BaseQueryResult<>(() -> result.stream(), dataCollectorFunction);
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
    }
}

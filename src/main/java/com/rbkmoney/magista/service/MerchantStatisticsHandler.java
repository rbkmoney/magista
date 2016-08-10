package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.merch_stat.DatasetTooBig;
import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv.Iface;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryParser;
import com.rbkmoney.magista.query.QueryResult;
import com.rbkmoney.magista.query.impl.QueryContextBuilder;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collector;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class MerchantStatisticsHandler implements Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private QueryParser queryParser;
    private QueryContextBuilder contextBuilder;

    @Override
    public StatResponse getPayments(StatRequest statRequest) throws InvalidRequest, DatasetTooBig, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getInvoices(StatRequest statRequest) throws InvalidRequest, DatasetTooBig, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getCustomers(StatRequest statRequest) throws InvalidRequest, DatasetTooBig, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getStatistics(StatRequest statRequest) throws InvalidRequest, DatasetTooBig, TException {
        return getStatResponse(statRequest);
    }

    private StatResponse getStatResponse(StatRequest statRequest) throws InvalidRequest {
        log.info("New stat request: {}" ,statRequest);
        try {
            Query query = queryParser.parse(statRequest.getDsl());
            QueryContext context = contextBuilder.getQueryContext(query);
            QueryResult<?, StatResponseData> qResult = query.execute(context);
            StatResponse statResponse = new StatResponse(qResult.getCollectedStream());
            log.debug("Stat response: {}", statRequest);
            return statResponse;
        } catch (Exception e) {
            log.error("Failed to process stat request", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        }
    }

}

package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.merch_stat.BadToken;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.endpoint.StatisticsServletIface;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.QueryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

import java.util.Arrays;

@Deprecated
@Slf4j
public class DeprecatedMerchantStatisticsHandler implements StatisticsServletIface {

    private final QueryProcessor<StatRequest, StatResponse> queryProcessor;

    public DeprecatedMerchantStatisticsHandler(QueryProcessor<StatRequest, StatResponse> queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @Override
    public StatResponse getPayments(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getRefunds(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getInvoices(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getCustomers(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getStatistics(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getPayouts(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getChargebacks(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getByQuery(StatRequest statRequest) throws TException {
        return getStatResponse(statRequest);
    }

    private StatResponse getStatResponse(StatRequest statRequest) throws InvalidRequest, BadToken {
        log.info("New stat request: {}", statRequest);
        try {
            StatResponse statResponse = queryProcessor.processQuery(statRequest);
            log.debug("Stat response: {}", statResponse);
            return statResponse;
        } catch (BadTokenException ex) {
            throw new BadToken(ex.getMessage());
        } catch (Exception e) {
            log.error("Failed to process stat request", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        }
    }
}

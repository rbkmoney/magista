package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.merch_stat.BadToken;
import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv.Iface;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.QueryProcessor;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class MerchantStatisticsHandler implements Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private QueryProcessor<StatRequest, StatResponse> queryProcessor;

    public MerchantStatisticsHandler(QueryProcessor<StatRequest, StatResponse> queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @Override
    public StatResponse getPayments(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getInvoices(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getCustomers(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getStatistics(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getPayouts(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    private StatResponse getStatResponse(StatRequest statRequest) throws InvalidRequest, BadToken {
        log.info("New stat request: {}" ,statRequest);
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

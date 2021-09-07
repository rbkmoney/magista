package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.DeprecatedSearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.service.DeprecatedTokenGenService;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {

    private final StatisticsDao statisticsDao;

    private final DeprecatedSearchDao searchDao;

    private final DeprecatedTokenGenService tokenGenService;

    public FunctionQueryContext(
            StatisticsDao statisticsDao,
            DeprecatedSearchDao searchDao,
            DeprecatedTokenGenService tokenGenService) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.tokenGenService = tokenGenService;
    }

    public StatisticsDao getStatisticsDao() {
        return statisticsDao;
    }

    public DeprecatedSearchDao getSearchDao() {
        return searchDao;
    }

    public DeprecatedTokenGenService getTokenGenService() {
        return tokenGenService;
    }
}

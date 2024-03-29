package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.DeprecatedSearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryContextFactory;
import com.rbkmoney.magista.service.DeprecatedTokenGenService;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryContextFactoryImpl implements QueryContextFactory {

    private final StatisticsDao statisticsDao;

    private final DeprecatedSearchDao searchDao;

    private final DeprecatedTokenGenService tokenGenService;

    public QueryContextFactoryImpl(
            StatisticsDao statisticsDao,
            DeprecatedSearchDao searchDao,
            DeprecatedTokenGenService tokenGenService) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.tokenGenService = tokenGenService;
    }

    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(statisticsDao, searchDao, tokenGenService);
    }
}

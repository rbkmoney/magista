package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryContextFactory;
import com.rbkmoney.magista.service.TokenGenService;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryContextFactoryImpl implements QueryContextFactory {

    private final StatisticsDao statisticsDao;

    private final SearchDao searchDao;

    private final TokenGenService tokenGenService;

    public QueryContextFactoryImpl(StatisticsDao statisticsDao, SearchDao searchDao, TokenGenService tokenGenService) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.tokenGenService = tokenGenService;
    }

    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(statisticsDao, searchDao, tokenGenService);
    }
}

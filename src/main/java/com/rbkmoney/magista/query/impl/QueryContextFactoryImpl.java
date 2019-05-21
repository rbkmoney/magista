package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryContextFactory;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryContextFactoryImpl implements QueryContextFactory {

    private final StatisticsDao statisticsDao;

    private final SearchDao searchDao;

    public QueryContextFactoryImpl(StatisticsDao statisticsDao, SearchDao searchDao) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
    }

    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(statisticsDao, searchDao);
    }
}

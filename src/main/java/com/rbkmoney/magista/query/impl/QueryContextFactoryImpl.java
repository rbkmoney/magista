package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryContextFactory;
import com.rbkmoney.magista.dao.StatisticsDao;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryContextFactoryImpl implements QueryContextFactory {

    public QueryContextFactoryImpl(StatisticsDao statisticsDao) {
        this.statisticsDao = statisticsDao;
    }

    private StatisticsDao statisticsDao;
    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(statisticsDao);
    }
}

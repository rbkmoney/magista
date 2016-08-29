package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.QueryContext;
import com.rbkmoney.magista.query2.QueryContextFactory;
import com.rbkmoney.magista.repository.dao.StatisticsDao;

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

package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryContextFactory;
import com.rbkmoney.magista.dao.StatisticsDao;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public class QueryContextFactoryImpl implements QueryContextFactory {

    private final StatisticsDao statisticsDao;

    private final ReportDao reportDao;

    private final SearchDao searchDao;

    public QueryContextFactoryImpl(StatisticsDao statisticsDao, SearchDao searchDao, ReportDao reportDao) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.reportDao = reportDao;
    }

    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(statisticsDao, searchDao, reportDao);
    }
}

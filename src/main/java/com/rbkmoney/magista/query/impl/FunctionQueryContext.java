package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.dao.StatisticsDao;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {

    private final StatisticsDao statisticsDao;

    private final SearchDao searchDao;

    private final ReportDao reportDao;

    public FunctionQueryContext(StatisticsDao statisticsDao, SearchDao searchDao, ReportDao reportDao) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.reportDao = reportDao;
    }

    public StatisticsDao getStatisticsDao() {
        return statisticsDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

    public ReportDao getReportDao() {
        return reportDao;
    }
}

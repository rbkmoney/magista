package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.service.TokenGenService;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {

    private final StatisticsDao statisticsDao;

    private final SearchDao searchDao;

    private final ReportDao reportDao;

    private final TokenGenService tokenGenService;

    public FunctionQueryContext(StatisticsDao statisticsDao, SearchDao searchDao, ReportDao reportDao, TokenGenService tokenGenService) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
        this.reportDao = reportDao;
        this.tokenGenService = tokenGenService;
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

    public TokenGenService getTokenGenService() {
        return tokenGenService;
    }
}

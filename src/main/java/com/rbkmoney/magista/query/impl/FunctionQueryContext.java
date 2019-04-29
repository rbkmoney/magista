package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryContext;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {

    private final StatisticsDao statisticsDao;

    private final SearchDao searchDao;

    public FunctionQueryContext(StatisticsDao statisticsDao, SearchDao searchDao) {
        this.statisticsDao = statisticsDao;
        this.searchDao = searchDao;
    }

    public StatisticsDao getStatisticsDao() {
        return statisticsDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

}

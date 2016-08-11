package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.Query;
import com.rbkmoney.magista.repository.dao.StatisticsDao;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class QueryContextBuilder {

    private StatisticsDao dao;

    public QueryContextBuilder(StatisticsDao dao) {
        this.dao = dao;
    }

    public FunctionQueryContext getQueryContext(Query query) {
        return new FunctionQueryContext(dao);
    }
}

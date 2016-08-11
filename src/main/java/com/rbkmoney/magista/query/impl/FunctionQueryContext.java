package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.repository.dao.StatisticsDao;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class FunctionQueryContext implements QueryContext {
    private StatisticsDao dao;

    public FunctionQueryContext(StatisticsDao dao) {
        this.dao = dao;
    }

    public StatisticsDao getDao() {
        return dao;
    }
}

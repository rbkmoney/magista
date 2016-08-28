package com.rbkmoney.magista.query2;

import java.util.List;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public interface CompositeQuery extends Query {
    List<Query> getChildQueries();

    void setChildQueries(List<Query> queries, boolean parallel);

    boolean isParallel();
    //Object execute(List<Object> results, Object context);
}

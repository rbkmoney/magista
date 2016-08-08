package com.rbkmoney.magista.dsl;

import java.util.List;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface CompositeQuery extends Query {

    List<Query> getQueries();

    boolean isParallel();
}

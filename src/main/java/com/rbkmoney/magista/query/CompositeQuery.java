package com.rbkmoney.magista.query2;

import java.util.List;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public interface CompositeQuery<T, CT> extends Query<T, CT> {
    List<Query> getChildQueries();

    default boolean isParallel() {
        return false;
    }

    default QueryResult<T, CT> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        throw new UnsupportedOperationException("Explicit implementation required");
    }

}

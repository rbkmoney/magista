package com.rbkmoney.magista.query;

import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public abstract class QueryResult<T, AT> {
    public abstract int expectedTotalCount();

    public abstract Stream<T> getDataStream();

    public abstract AT getCollectedStream();

}


package com.rbkmoney.magista.query;

import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface QueryResult<T, AT> {
    int expectedTotalCount();

    Stream<T> getDataStream();

    AT getCollectedStream();

}


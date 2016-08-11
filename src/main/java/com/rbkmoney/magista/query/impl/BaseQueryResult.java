package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.query.QueryResult;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class BaseQueryResult<T, AT> implements QueryResult<T, AT> {
    private final int expectedTotalCount;
    private final Supplier<Stream<T>> dataStream;
    private final BiFunction<Stream<T>, QueryResult, Supplier<AT>> dataCollector;

    public BaseQueryResult(int expectedTotalCount, Supplier<Stream<T>> dataStream, BiFunction<Stream<T>, QueryResult, Supplier<AT>> dataCollector) {
        this.expectedTotalCount = expectedTotalCount;
        this.dataStream = dataStream;
        this.dataCollector = dataCollector;
    }

    public BaseQueryResult(Supplier<Stream<T>> dataStream, BiFunction<Stream<T>, QueryResult, Supplier<AT>> dataCollector) {
        this(-1, dataStream, dataCollector);
    }

    @Override
    public int expectedTotalCount() {
        return expectedTotalCount;
    }

    @Override
    public Stream<T> getDataStream() {
        return dataStream.get();
    }

    @Override
    public AT getCollectedStream() {
        return dataCollector.apply(getDataStream(), this).get();
    }
}

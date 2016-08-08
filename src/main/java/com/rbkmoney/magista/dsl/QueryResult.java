package com.rbkmoney.magista.dsl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public abstract class QueryResult<T> {
    public abstract Stream<T> getDataStream();

    public List<T> getData() {
        return getDataStream().collect(Collectors.toList());
    }
}

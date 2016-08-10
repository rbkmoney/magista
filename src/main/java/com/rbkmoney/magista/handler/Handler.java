package com.rbkmoney.magista.handler;

import com.rbkmoney.thrift.filter.Filter;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface Handler<T> {

    default boolean accept(T value) {
        return getFilter().match(value);
    }

    void handle(T value);

    Filter getFilter();

}

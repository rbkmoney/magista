package com.rbkmoney.magista.handler;

import com.rbkmoney.thrift.filter.Filter;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface Handler<T, P> {

    default boolean accept(T value) {
        return getFilter().match(value);
    }

    P handle(T value);

    Filter getFilter();

}

package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.magista.event.ChangeType;

public interface Mapper<C, P, R> {

    default boolean accept(C change) {
        return getChangeType().getFilter().match(change);
    }

    R map(C change, P parent);

    ChangeType getChangeType();

}

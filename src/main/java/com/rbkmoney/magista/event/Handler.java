package com.rbkmoney.magista.event;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface Handler<C, P, R> {

    default boolean accept(C change) {
        return getChangeType().getFilter().match(change);
    }

    R handle(C change, P parent);

    ChangeType getChangeType();

}

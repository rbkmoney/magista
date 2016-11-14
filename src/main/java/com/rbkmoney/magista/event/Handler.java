package com.rbkmoney.magista.event;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface Handler<T> {

    default boolean accept(T event) {
        return getEventType().getFilter().match(event);
    }

    EventContext handle(T event);

    EventType getEventType();

}

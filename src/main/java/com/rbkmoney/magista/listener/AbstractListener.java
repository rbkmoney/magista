package com.rbkmoney.magista.listener;

import com.rbkmoney.magista.event.Handler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractListener {

    private final List<Handler> handlers;

    protected  <C> Handler getHandler(C change) {
        for (Handler handler : handlers) {
            if (handler.accept(change)) {
                return handler;
            }
        }
        return null;
    }
}

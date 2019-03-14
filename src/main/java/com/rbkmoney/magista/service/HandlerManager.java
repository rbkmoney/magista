package com.rbkmoney.magista.service;

import com.rbkmoney.magista.event.Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HandlerManager {

    private final List<Handler> handlers;

    public <C> Handler getHandler(C change) {
        for (Handler handler : handlers) {
            if (handler.accept(change)) {
                return handler;
            }
        }
        return null;
    }
}

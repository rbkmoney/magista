package com.rbkmoney.magista.service;

import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.handler.impl.UnsupportedBatchHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HandlerManager {

    private final BatchHandler DEFAULT_BATCH_HANDLER = new UnsupportedBatchHandler();

    private final List<BatchHandler> handlers;

    public <C> BatchHandler getHandler(C change) {
        for (BatchHandler handler : handlers) {
            if (handler.accept(change)) {
                return handler;
            }
        }
        return DEFAULT_BATCH_HANDLER;
    }
}

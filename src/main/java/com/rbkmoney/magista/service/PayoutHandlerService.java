package com.rbkmoney.magista.service;

import com.rbkmoney.magista.event.handler.PayoutHandler;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutHandlerService {

    private final List<PayoutHandler> payoutHandlers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<Event> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(Event event) {
        PayoutChange change = event.getPayoutChange();
        payoutHandlers.stream()
                .filter(handler -> handler.accept(change))
                .forEach(handler -> handler.handle(change, event));
    }
}

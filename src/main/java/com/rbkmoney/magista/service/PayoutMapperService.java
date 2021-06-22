package com.rbkmoney.magista.service;

import com.rbkmoney.magista.event.mapper.PayoutMapper;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.EventPayload;
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
public class PayoutMapperService {

    private final List<PayoutMapper> payoutMappers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<Event> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(Event event) {
        if (event.isSetPayload()) {
            EventPayload eventPayload = event.getPayload();
            for (int i = 0; i < eventPayload.getChanges().size(); i++) {
                PayoutChange change = eventPayload.getChanges().get(i);
                payoutMappers.stream()
                        .filter(handler -> handler.accept(change))
                        .forEach(handler -> handler.map(change, event));
            }
        }
    }
}

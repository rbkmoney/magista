package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.magista.event.Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutListener {

    private final List<Handler> handlers;

    @KafkaListener(topics = "${kafka.payout.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload byte[] message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long key) {
        StockEvent stockEvent = Geck.msgPackToTBase(message, StockEvent.class);
        Event event = stockEvent.getSourceEvent().getPayoutEvent();
        if (event.getPayload().isSetPayoutChanges()) {
            for (PayoutChange payoutChange : event.getPayload().getPayoutChanges()) {
                Handler handler = getHandler(payoutChange);
                if (handler != null) {
                    log.info("Start payout event handling, id='{}', eventType='{}', handlerType='{}'",
                            key, handler.getChangeType(), handler.getClass().getSimpleName());
                    handler.handle(payoutChange, stockEvent);
                }
            }
        }
    }

    private <C> Handler getHandler(C change) {
        for (Handler handler : handlers) {
            if (handler != null && handler.accept(change)) {
                return handler;
            }
        }
        return null;
    }
}

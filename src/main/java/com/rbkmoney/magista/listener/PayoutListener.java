package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.service.HandlerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutListener implements MessageListener {

    private final HandlerManager handlerManager;
    private final SafeMessageConsumer safeMessageConsumer;
    private final SourceEventParser eventParser;

    @KafkaListener(topics = "${kafka.payout.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload MachineEvent message, Acknowledgment ack) {
        safeMessageConsumer.safeMessageHandler(this::handle, message, ack);
    }

    @Override
    public void handle(MachineEvent message, Acknowledgment ack) {
        SourceEvent sourceEvent = eventParser.parseEvent(message);
        EventPayload payload = sourceEvent.getPayoutEvent().getPayload();
        if (payload.isSetPayoutChanges()) {
            for (PayoutChange payoutChange : payload.getPayoutChanges()) {
                Handler handler = handlerManager.getHandler(payoutChange);
                if (handler != null) {
                    handler.handle(payoutChange, sourceEvent).execute();
                }
            }
        }
    }
}

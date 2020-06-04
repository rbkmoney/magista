package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.magista.event.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PayoutEventFlow extends AbstractEventPayoutFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PayoutEventFlow(List<Handler> handlers, DefaultPollingEventPublisherBuilder defaultPollingEventPublisherBuilder, int threadPoolSize, int queueLimit, long timeout) {
        super("PayoutEvent", handlers, defaultPollingEventPublisherBuilder, threadPoolSize, queueLimit, timeout);
    }

    public void processEvent(Event event) {
        if (event.getPayload().isSetPayoutChanges()) {
            for (PayoutChange payoutChange : event.getPayload().getPayoutChanges()) {
                Handler handler = getHandler(payoutChange);
                if (handler != null) {
                    log.info("Start payout event handling, id='{}', eventType='{}', handlerType='{}'",
                            event.getId(), handler.getChangeType(), handler.getClass().getSimpleName());
                    submitAndPutInQueue(() -> handler.handle(payoutChange, event));
                }
            }
        }
    }
}

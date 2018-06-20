package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.magista.event.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PayoutEventFlow extends AbstractEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PayoutEventFlow(List<Handler> handlers, int threadPoolSize, int queueLimit, long timeout) {
        super("PayoutEvent", handlers, threadPoolSize, queueLimit, timeout);
    }

    public void processEvent(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getPayoutEvent();
        if (event.getPayload().isSetPayoutChanges()) {
            for (PayoutChange payoutChange : event.getPayload().getPayoutChanges()) {
                List<Handler> handlers = getHandlers(payoutChange);
                handlers.forEach(
                        handler -> {
                            log.info("Start payout event handling, id='{}', eventType='{}', handlerType='{}'",
                                    stockEvent.getSourceEvent().getPayoutEvent().getId(), handler.getChangeType(), handler.getClass().getSimpleName());
                            submitAndPutInQueue(() -> handler.handle(payoutChange, stockEvent));
                        }
                );
            }
        }
    }
}

package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.magista.event.mapper.Mapper;
import com.rbkmoney.magista.event.mapper.PayoutMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PayoutEventFlow extends AbstractEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PayoutEventFlow(List<? extends Mapper> mappers,
                           DefaultPollingEventPublisherBuilder defaultPollingEventPublisherBuilder, int threadPoolSize,
                           int queueLimit, long timeout) {
        super("PayoutEvent", mappers, defaultPollingEventPublisherBuilder, threadPoolSize, queueLimit, timeout);
    }

    public void processEvent(Event event) {
        if (event.getPayload().isSetPayoutChanges()) {
            for (PayoutChange payoutChange : event.getPayload().getPayoutChanges()) {
                PayoutMapper mapper = (PayoutMapper) getMapper(payoutChange);
                if (mapper != null) {
                    log.info("Start payout event handling, id='{}', eventType='{}', handlerType='{}'",
                            event.getId(), mapper.getChangeType(), mapper.getClass().getSimpleName());
                    submitAndPutInQueue(() -> mapper.map(payoutChange, event));
                }
            }
        }
    }
}

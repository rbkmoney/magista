package com.rbkmoney.magista.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.magista.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    EventService eventService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        Long lastEventId = eventService.getLastEventId();
        if (lastEventId != null) {
            eventIDRange.setFromExclusive(lastEventId);
        }
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));

        eventPublisher.subscribe(new DefaultSubscriberConfig(eventFlowFilter));
    }

}

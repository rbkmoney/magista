package com.rbkmoney.magista.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.service.InvoiceEventService;
import com.rbkmoney.magista.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher eventPublisher;
    private final InvoiceEventService invoiceEventService;
    private final EventSaver eventSaver;


    @Value("${bm.pooling.enabled}")
    private boolean poolingEnabled;

    @Autowired
    public OnStart(EventPublisher eventPublisher, EventSaver eventSaver, InvoiceEventService invoiceEventService) {
        this.eventPublisher = eventPublisher;
        this.invoiceEventService = invoiceEventService;
        this.eventSaver = eventSaver;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (poolingEnabled) {
            EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
            Optional<Long> lastEventIdOptional = invoiceEventService.getLastEventId();
            if (lastEventIdOptional.isPresent()) {
                eventIDRange.setFromExclusive(lastEventIdOptional.get());
            }
            EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
            eventPublisher.subscribe(new DefaultSubscriberConfig(eventFlowFilter));
        }
        eventSaver.start();
    }

}

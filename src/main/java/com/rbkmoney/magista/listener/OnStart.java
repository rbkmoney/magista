package com.rbkmoney.magista.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.magista.service.InvoiceEventService;
import com.rbkmoney.magista.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher processingEventPublisher;
    private final EventPublisher payoutEventPublisher;

    private final InvoiceEventService invoiceEventService;
    private final ProcessingService processingService;

    @Autowired
    public OnStart(EventPublisher processingEventPublisher,
                   EventPublisher payoutEventPublisher,
                   ProcessingService processingService,
                   InvoiceEventService invoiceEventService) {
        this.processingEventPublisher = processingEventPublisher;
        this.payoutEventPublisher = payoutEventPublisher;

        this.invoiceEventService = invoiceEventService;
        this.processingService = processingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribeToInvoiceEventStock();
        subscribeToPayoutEventStock();
        processingService.start();
    }

    private void subscribeToInvoiceEventStock() {
        processingEventPublisher.subscribe(buildSubscriberConfig(invoiceEventService.getLastEventId()));
    }

    private void subscribeToPayoutEventStock() {
        payoutEventPublisher.subscribe(buildSubscriberConfig(Optional.of(0L)));
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        if (lastEventIdOptional.isPresent()) {
            eventIDRange.setFromExclusive(lastEventIdOptional.get());
        }
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }

}

package com.rbkmoney.magista.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.magista.service.InvoiceEventService;
import com.rbkmoney.magista.service.PayoutEventService;
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

    private final EventPublisher processingEventPublisher;
    private final EventPublisher payoutEventPublisher;

    private final ProcessingService processingService;

    private final InvoiceEventService invoiceEventService;
    private final PayoutEventService payoutEventService;

    @Value("${bm.pooling.enabled}")
    private boolean poolingEnabled;

    @Autowired
    public OnStart(EventPublisher processingEventPublisher,
                   EventPublisher payoutEventPublisher,
                   ProcessingService processingService,
                   InvoiceEventService invoiceEventService,
                   PayoutEventService payoutEventService) {
        this.processingEventPublisher = processingEventPublisher;
        this.payoutEventPublisher = payoutEventPublisher;

        this.invoiceEventService = invoiceEventService;
        this.payoutEventService = payoutEventService;

        this.processingService = processingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (poolingEnabled) {
            subscribeToInvoiceEventStock();
            subscribeToPayoutEventStock();
        }
        processingService.start();
    }

    private void subscribeToInvoiceEventStock() {
        processingEventPublisher.subscribe(buildSubscriberConfig(invoiceEventService.getLastEventId()));
    }

    private void subscribeToPayoutEventStock() {
        payoutEventPublisher.subscribe(buildSubscriberConfig(payoutEventService.getLastEventId()));
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

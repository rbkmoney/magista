package com.rbkmoney.magista.listener;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
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
    SubscriberConfig subscriberConfig;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        eventPublisher.subscribe(subscriberConfig);
    }

}

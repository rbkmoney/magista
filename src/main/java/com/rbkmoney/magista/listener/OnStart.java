package com.rbkmoney.magista.listener;

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
    EventService eventService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        eventService.startPooling();
    }

}

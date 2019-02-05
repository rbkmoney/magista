package com.rbkmoney.magista.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @PreDestroy
    public void stop() {
        kafkaListenerEndpointRegistry.stop();
    }

}

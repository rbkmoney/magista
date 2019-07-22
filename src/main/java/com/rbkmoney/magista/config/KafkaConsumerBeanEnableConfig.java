package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.InvoicingKafkaListener;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.List;

@EnableKafka
@Configuration
public class KafkaConsumerBeanEnableConfig {

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.invoice.enabled", havingValue = "true")
    public InvoicingKafkaListener invoicingEventsKafkaListener(List<Handler> handlers,
                                                             MachineEventParser<EventPayload> parser) {
        return new InvoicingKafkaListener(handlers, parser);
    }

}

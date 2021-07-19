package com.rbkmoney.magista.config;

import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.listener.InvoiceListener;
import com.rbkmoney.magista.listener.InvoiceTemplateListener;
import com.rbkmoney.magista.service.HandlerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConsumerBeanEnableConfig {

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.invoice-template.consume.enabled", havingValue = "true")
    public InvoiceTemplateListener invoiceTemplateListener(
            HandlerManager handlerManager,
            SourceEventParser eventParser) {
        return new InvoiceTemplateListener(handlerManager, eventParser);
    }

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.invoicing.consume.enabled", havingValue = "true")
    public InvoiceListener invoiceListener(
            HandlerManager handlerManager,
            SourceEventParser eventParser) {
        return new InvoiceListener(handlerManager, eventParser);
    }
}

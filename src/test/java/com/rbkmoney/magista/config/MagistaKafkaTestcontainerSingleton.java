package com.rbkmoney.magista.config;

import com.rbkmoney.testcontainers.annotations.kafka.KafkaTestcontainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaTestcontainer(
        instanceMode = KafkaTestcontainer.InstanceMode.SINGLETON,
        properties = {
                "kafka.topics.invoicing.consume.enabled=true",
                "kafka.topics.invoice-template.consume.enabled=true",
                "kafka.topics.pm-events-payout.consume.enabled=true",
                "kafka.state.cache.size=0"},
        topicsKeys = {
                "kafka.topics.invoicing.id",
                "kafka.topics.invoice-template.id",
                "kafka.topics.pm-events-payout.id"})
public @interface MagistaKafkaTestcontainerSingleton {
}

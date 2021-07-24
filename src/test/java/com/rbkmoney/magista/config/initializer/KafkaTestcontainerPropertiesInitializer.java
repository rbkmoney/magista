package com.rbkmoney.magista.config.initializer;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;

import static com.rbkmoney.magista.config.testcontainer.KafkaTestcontainerFactory.container;

public class KafkaTestcontainerPropertiesInitializer extends ConfigDataApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "kafka.bootstrap-servers=" + container().getBootstrapServers(),
                "kafka.ssl.enabled=false",
                "kafka.topics.invoicing.consume.enabled=true",
                "kafka.topics.invoice-template.consume.enabled=true",
                "kafka.topics.pm-events-payout.consume.enabled=true",
                "kafka.state.cache.size=0")
                .applyTo(applicationContext);
    }
}

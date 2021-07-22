package com.rbkmoney.magista.config;

import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.payout.manager.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
                MagistaApplication.class,
                AbstractKafkaAndDaoConfig.KafkaProducerConfig.class},
        initializers = {
                AbstractKafkaAndDaoConfig.KafkaPropertiesInitializer.class,
                AbstractKafkaAndDaoConfig.DataSourcePropertiesInitializer.class})
@DirtiesContext
@TestPropertySource("classpath:application.yml")
@Transactional
@Slf4j
public abstract class AbstractKafkaAndDaoConfig extends AbstractKafkaSingletonContainerConfig {

    @LocalServerPort
    public int port;

    @TestConfiguration
    public static class KafkaProducerConfig {

        @Value("${kafka.bootstrap-servers}")
        private String bootstrapAddress;

        @Bean
        public KafkaTemplate<String, SinkEvent> sinkEventProducer() {
            return new KafkaTemplate<>(thriftProducerFactory());
        }

        @Bean
        public KafkaTemplate<String, Event> payoutProducer() {
            return new KafkaTemplate<>(thriftProducerFactory());
        }

        private <T extends TBase<?, ?>> ProducerFactory<String, T> thriftProducerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
            props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
            return new DefaultKafkaProducerFactory<>(props);
        }
    }

    public static class KafkaPropertiesInitializer extends ConfigDataApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "kafka.bootstrap-servers=" + KAFKA_CONTAINER.getBootstrapServers(),
                    "kafka.ssl.enabled=false",
                    "kafka.topics.invoicing.consume.enabled=true",
                    "kafka.topics.invoice-template.consume.enabled=true",
                    "kafka.topics.pm-events-payout.consume.enabled=true",
                    "kafka.state.cache.size=0")
                    .applyTo(applicationContext);
        }
    }

    public static class DataSourcePropertiesInitializer extends ConfigDataApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "spring.flyway.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "spring.flyway.user=" + POSTGRESQL_CONTAINER.getUsername(),
                    "spring.flyway.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "flyway.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "flyway.user=" + POSTGRESQL_CONTAINER.getUsername(),
                    "flyway.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "token-gen.key=" + "jXnZr4u7x!A%D*G-KaPvSgVkYp3s5v8t/B?E(H+MbQeThWmZq4t7w9z$C&F)J@Nc",
                    "cache.invoiceData.size=10000",
                    "cache.paymentData.size=10000",
                    "payouter.pooling.enabled=false")
                    .applyTo(configurableApplicationContext);
        }
    }
}

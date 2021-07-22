package com.rbkmoney.magista.config;

import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.payout.manager.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = {
                AbstractKafkaAndDaoConfig.KafkaPropertiesInitializer.class,
                AbstractKafkaAndDaoConfig.DataSourcePropertiesInitializer.class})
@DirtiesContext
@TestPropertySource("classpath:application.yml")
@Transactional
@Slf4j
public abstract class AbstractKafkaAndDaoConfig extends AbstractKafkaSingletonContainerConfig {

    private static final String PRODUCER_CLIENT_ID = "producer-service-test-" + UUID.randomUUID();

    @LocalServerPort
    public int port;

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

    public void producePayout(String topicName, Event event) {
        try (Producer<String, Event> producer = createProducer()) {
            var producerRecord = new ProducerRecord<>(topicName, event.getPayoutId(), event);
            producer.send(producerRecord).get();
            log.info("Produce message to {} event: {}", topicName, event);
        } catch (Exception e) {
            log.error("Error when produce message to {} e:", topicName, e);
        }
    }

    public void produce(String topicName, SinkEvent sinkEvent) {
        try (Producer<String, SinkEvent> producer = createProducer()) {
            var sourceId = sinkEvent.getEvent().getSourceId();
            var producerRecord = new ProducerRecord<>(topicName, sourceId, sinkEvent);
            producer.send(producerRecord).get();
            log.info("Produce message to {} sinkEvent: {}", topicName, sinkEvent);
        } catch (Exception e) {
            log.error("Error when produce message to {} e:", topicName, e);
        }
    }

    protected <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}

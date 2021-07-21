package com.rbkmoney.magista.config;

import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.serde.PayoutEventDeserializer;
import com.rbkmoney.magista.serde.SinkEventDeserializer;
import com.rbkmoney.payout.manager.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;

@SpringBootTest
@Testcontainers
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = AbstractKafkaConfig.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource("classpath:application.yml")
@Slf4j
public abstract class AbstractKafkaConfig extends AbstractDaoConfig {

    private static final String PRODUCER_CLIENT_ID = "producer-service-test-" + UUID.randomUUID();
    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @Container
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName
                    .parse(CONFLUENT_IMAGE_NAME)
                    .withTag(CONFLUENT_PLATFORM_VERSION))
            .withEmbeddedZookeeper();

    public static class Initializer extends ConfigDataApplicationContextInitializer {

        private static final String CONSUMER_GROUP_ID = "init-topic-consumer-test-" + UUID.randomUUID();

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
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            initTopic(
                    environment.getProperty("kafka.topics.invoicing.id"),
                    SinkEventDeserializer.class);
            initTopic(
                    environment.getProperty("kafka.topics.invoice-template.id"),
                    SinkEventDeserializer.class);
            initTopic(
                    environment.getProperty("kafka.topics.pm-events-payout.id"),
                    PayoutEventDeserializer.class);
        }

        private <T> void initTopic(String topicName, Class clazz) {
            try (Consumer<String, T> consumer = createConsumer(clazz)) {
                consumer.subscribe(Collections.singletonList(topicName));
                consumer.poll(Duration.ofMillis(500L));
                log.info("Init topic '{}'", topicName);
            } catch (Exception e) {
                log.error("Error when init topic '{}' e:", topicName, e);
            }
        }

        private <T> Consumer<String, T> createConsumer(Class clazz) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST.name().toLowerCase());
            return new KafkaConsumer<>(props);
        }
    }

    protected void producePayout(String topicName, Event event) {
        try (Producer<String, Event> producer = createProducer()) {
            var producerRecord = new ProducerRecord<>(topicName, event.getPayoutId(), event);
            producer.send(producerRecord).get();
            log.info("Produce message to {} event: {}", topicName, event);
        } catch (Exception e) {
            log.error("Error when produce message to {} e:", topicName, e);
        }
    }

    protected void produce(String topicName, SinkEvent sinkEvent) {
        try (Producer<String, SinkEvent> producer = createProducer()) {
            var sourceId = sinkEvent.getEvent().getSourceId();
            var producerRecord = new ProducerRecord<>(topicName, sourceId, sinkEvent);
            producer.send(producerRecord).get();
            log.info("Produce message to {} sinkEvent: {}", topicName, sinkEvent);
        } catch (Exception e) {
            log.error("Error when produce message to {} e:", topicName, e);
        }
    }

    private <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}

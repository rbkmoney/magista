package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.serde.PayoutEventDeserializer;
import com.rbkmoney.magista.serde.SinkEventDeserializer;
import com.rbkmoney.magista.service.HandlerManager;
import com.rbkmoney.payout.manager.*;
import com.rbkmoney.payout.manager.domain.CurrencyRef;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandom;
import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = {
                KafkaListenerTest.KafkaInitializer.class,
                KafkaListenerTest.PostgresInitializer.class})
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource("classpath:application.yml")
@Slf4j
public class KafkaListenerTest {

    @Value("${kafka.topics.invoicing.id}")
    private String invoicingTopicName;

    @Value("${kafka.topics.invoice-template.id}")
    private String invoiceTemplateTopicName;

    @Value("${kafka.topics.pm-events-payout.id}")
    private String payoutTopicName;

    @MockBean
    public HandlerManager handlerManager;

    @MockBean
    public SourceEventParser eventParser;

    private static final String PRODUCER_CLIENT_ID = "producer-service-test-" + UUID.randomUUID();
    private static final String INIT_TOPIC_CONSUMER_GROUP_ID = "init-topic-consumer-test-" + UUID.randomUUID();
    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";
    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "11.4";

    @LocalServerPort
    public int port;

    @Value("${kafka.bootstrap-servers}")
    public String bootstrapServers;

    @Container
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName
                    .parse(CONFLUENT_IMAGE_NAME)
                    .withTag(CONFLUENT_PLATFORM_VERSION))
            .withEmbeddedZookeeper();

    @Container
    @SuppressWarnings("rawtypes")
    public static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName
                    .parse(POSTGRESQL_IMAGE_NAME)
                    .withTag(POSTGRESQL_VERSION));

    public static class KafkaInitializer extends ConfigDataApplicationContextInitializer {

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
    }

    public static class PostgresInitializer extends ConfigDataApplicationContextInitializer {

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

    public static <T> Consumer<String, T> createConsumer(Class clazz) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, INIT_TOPIC_CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST.name().toLowerCase());
        return new KafkaConsumer<>(props);
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

    public static <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static <T> T random(Class<T> type, String... excludedFields) {
        return aNewEnhancedRandom().nextObject(type, excludedFields);
    }

    public static <T> Stream<T> randomStreamOf(int amount, Class<T> type, String... excludedFields) {
        return aNewEnhancedRandom().objects(type, amount, excludedFields);
    }

    @Test
    public void shouldInvoicingSinkEventListen() throws InterruptedException {
        var message = new MachineEvent();
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceId("source_id");
        message.setSourceNs("source_ns");
        var data = new com.rbkmoney.machinegun.msgpack.Value();
        data.setBin(new byte[0]);
        message.setData(data);
        var sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_changes(List.of()));
        produce(invoicingTopicName, sinkEvent);
        Thread.sleep(3000L);
        verify(eventParser, times(1)).parseEvent(any());
    }

    @Test
    public void shouldInvoiceTemplateSinkEventListen() throws InterruptedException {
        var message = new MachineEvent();
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceId("source_id");
        message.setSourceNs("source_ns");
        var data = new com.rbkmoney.machinegun.msgpack.Value();
        data.setBin(new byte[0]);
        message.setData(data);
        var sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);
        when(eventParser.parseEvent(any())).thenReturn(EventPayload.invoice_template_changes(List.of()));
        produce(invoiceTemplateTopicName, sinkEvent);
        Thread.sleep(3000L);
        verify(eventParser, times(1)).parseEvent(any());
    }

    @Test
    public void shouldPayoutEventListen() throws InterruptedException {
        Event event = new Event();
        event.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        event.setPayoutId("payout_id");
        event.setSequenceId(1);
        Payout payout = new Payout()
                .setPayoutId("payout_id")
                .setPartyId("1")
                .setShopId("SHOP_ID")
                .setStatus(PayoutStatus.paid(new PayoutPaid()))
                .setPayoutToolId("111")
                .setFee(0L)
                .setAmount(10L)
                .setCurrency(new CurrencyRef()
                        .setSymbolicCode("RUB"))
                .setCashFlow(Collections.emptyList())
                .setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        payout.setPayoutId("payout_id");
        event.setPayoutChange(PayoutChange.created(new PayoutCreated(payout)));
        event.setPayout(payout);
        producePayout(payoutTopicName, event);
        Thread.sleep(3000L);
    }
}

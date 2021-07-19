package com.rbkmoney.magista.config;

import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.serde.MachineEventSerializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Testcontainers
@ContextConfiguration(
        classes = AbstractKafkaConfig.TestKafkaProduceConfig.class,
        initializers = AbstractKafkaConfig.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource("classpath:application.yml")
public abstract class AbstractKafkaConfig extends AbstractDaoConfig {

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";
    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "6.1.2";

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.invoicing.name}")
    public String invoicingTopic;

    @Value("${kafka.topics.invoice-template.name}")
    public String invoiceTemplateTopic;

    @Container
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName
                    .parse(CONFLUENT_IMAGE_NAME)
                    .withTag(CONFLUENT_PLATFORM_VERSION))
            .withEmbeddedZookeeper();

    @TestConfiguration
    @Import(MagistaApplication.class)
    public static class TestKafkaProduceConfig {

        @Value("${kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Value("${kafka.topics.invoicing.name}")
        private String invoicingTopic;

        @Value("${kafka.topics.invoice-template.name}")
        private String invoiceTemplateTopic;

        @Bean
        public ProducerFactory<String, SinkEvent> transactionProducerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id");
            props.put(ProducerConfig.ACKS_CONFIG, "1");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MachineEventSerializer.class);
            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        public KafkaTemplate<String, SinkEvent> transactionKafkaTemplate(
                ProducerFactory<String, SinkEvent> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        public KafkaAdmin adminClient() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            return new KafkaAdmin(configs);
        }

        @Bean
        public NewTopic invoicingTopic() {
            return TopicBuilder.name(invoicingTopic)
                    .partitions(1)
                    .replicas(1)
                    .build();
        }

        @Bean
        public NewTopic invoiceTemplateTopic() {
            return TopicBuilder.name(invoiceTemplateTopic)
                    .partitions(1)
                    .replicas(1)
                    .build();
        }
    }

    public static class Initializer extends ConfigDataApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap-servers=" + KAFKA_CONTAINER.getBootstrapServers(),
                            "kafka.ssl.enabled=false",
                            "kafka.topics.invoicing.consume.enabled=true",
                            "kafka.topics.invoice-template.consume.enabled=true")
                    .applyTo(applicationContext);
        }
    }
}

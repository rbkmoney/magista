package com.rbkmoney.magista.config;

import com.rbkmoney.magista.exception.KafkaStartingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public abstract class AbstractKafkaSingletonContainerConfig extends AbstractDbSingletonContainerConfig {

    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    //    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";
    private static final String CONFLUENT_PLATFORM_VERSION = "6.2.0";

    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName
                    .parse(CONFLUENT_IMAGE_NAME)
                    .withTag(CONFLUENT_PLATFORM_VERSION))
            .withEmbeddedZookeeper();

    static {
        startKafkaContainer();
        var topics = loadActualTopicNamesFromYmlProperties();
        createTopics(topics);
        parseAndCheckCreatedTopicsFromKafkaContainer(topics);
    }

    private static void startKafkaContainer() {
        Startables.deepStart(Stream.of(KAFKA_CONTAINER))
                .join();
        assertThat(KAFKA_CONTAINER.isRunning())
                .isTrue();
    }

    private static List<String> loadActualTopicNamesFromYmlProperties() {
        var properties = loadYmlProperties("application.yml");
        return List.of(
                properties.getProperty("kafka.topics.invoicing.id"),
                properties.getProperty("kafka.topics.invoice-template.id"),
                properties.getProperty("kafka.topics.pm-events-payout.id"));
    }

    private static void createTopics(List<String> topics) {
        try (var admin = createAdminClient()) {
            var newTopics = topics.stream()
                    .map(topic -> new NewTopic(topic, 1, (short) 1))
                    .peek(newTopic -> log.info(newTopic.toString()))
                    .collect(Collectors.toList());
            var topicsResult = admin.createTopics(newTopics);
            // wait until everyone is created or timeout
            topicsResult.all().get(30, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            throw new KafkaStartingException("Error when topic creating, ", ex);
        }
    }

    private static void parseAndCheckCreatedTopicsFromKafkaContainer(List<String> topics) {
        try {
            var showCreatedTopics = "/usr/bin/kafka-topics --bootstrap-server=localhost:9092 --list";
            var stdout = KAFKA_CONTAINER.execInContainer("/bin/sh", "-c", showCreatedTopics)
                    .getStdout();
            assertThat(stdout)
                    .contains(topics);
        } catch (IOException | InterruptedException ex) {
            throw new KafkaStartingException("Error when execInContainer, ", ex);
        }
    }

    private static Properties loadYmlProperties(String path) {
        try {
            var classLoader = AbstractKafkaSingletonContainerConfig.class.getClassLoader();
            var properties = new Properties();
            properties.putAll(getSources(new ClassPathResource(path, classLoader)));
            return properties;
        } catch (IOException ex) {
            throw new KafkaStartingException("Error when loading properties, ", ex);
        }
    }

    private static Map<String, Object> getSources(ClassPathResource resource) throws IOException {
        //noinspection unchecked
        return ((Map<String, OriginTrackedValue>) new YamlPropertySourceLoader()
                .load(resource.getFilename(), resource)
                .get(0)
                .getSource())
                .entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static AdminClient createAdminClient() {
        var properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        return AdminClient.create(properties);
    }
}

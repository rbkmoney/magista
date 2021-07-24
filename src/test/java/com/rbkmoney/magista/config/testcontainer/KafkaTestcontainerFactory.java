package com.rbkmoney.magista.config.testcontainer;

import com.rbkmoney.magista.exception.KafkaStartingException;
import lombok.Synchronized;
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
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class KafkaTestcontainerFactory {

    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "6.2.0";

    private KafkaContainer kafkaContainer;

    public static KafkaContainer container() {
        return instance().getOrInitAndStartContainer();
    }

    private static KafkaTestcontainerFactory instance() {
        return KafkaTestcontainerFactory.SingletonHolder.INSTANCE;
    }

    @Synchronized
    private KafkaContainer getOrInitAndStartContainer() {
        if (kafkaContainer != null) {
            return kafkaContainer;
        }
        KafkaContainer container = new KafkaContainer(
                DockerImageName
                        .parse(CONFLUENT_IMAGE_NAME)
                        .withTag(CONFLUENT_PLATFORM_VERSION))
                .withEmbeddedZookeeper();
        startContainer(container);
        var topics = loadActualTopicNamesFromYmlProperties();
        createTopics(container, topics);
        parseAndCheckCreatedTopicsFromKafkaContainer(container, topics);
        kafkaContainer = container;
        return kafkaContainer;
    }

    private void startContainer(KafkaContainer container) {
        Startables.deepStart(Stream.of(container))
                .join();
        assertThat(container.isRunning())
                .isTrue();
    }

    private List<String> loadActualTopicNamesFromYmlProperties() {
        var properties = loadYmlProperties("application.yml");
        List<String> topics = List.of(
                properties.get("kafka.topics.invoicing.id"),
                properties.get("kafka.topics.invoice-template.id"),
                properties.get("kafka.topics.pm-events-payout.id")).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        assertThat(topics).hasSize(3);
        return topics;
    }

    private Map<String, String> loadYmlProperties(String path) {
        try {
            var classPathResource = new ClassPathResource(path, getClass().getClassLoader());
            //noinspection unchecked
            return ((Map<String, OriginTrackedValue>) new YamlPropertySourceLoader()
                    .load(classPathResource.getFilename(), classPathResource)
                    .get(0)
                    .getSource())
                    .entrySet().stream()
                    .filter(entry -> entry.getValue().getValue() instanceof String)
                    .map(entry -> Map.entry(entry.getKey(), (String) entry.getValue().getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException ex) {
            throw new KafkaStartingException("Error when loading properties, ", ex);
        }
    }

    private void createTopics(KafkaContainer container, List<String> topics) {
        try (var admin = createAdminClient(container)) {
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

    private AdminClient createAdminClient(KafkaContainer container) {
        var properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers());
        return AdminClient.create(properties);
    }

    private void parseAndCheckCreatedTopicsFromKafkaContainer(KafkaContainer container, List<String> topics) {
        try {
            var showCreatedTopics = "/usr/bin/kafka-topics --bootstrap-server=0.0.0.0:9092 --list";
            var stdout = container.execInContainer("/bin/sh", "-c", showCreatedTopics)
                    .getStdout();
            assertThat(stdout)
                    .contains(topics);
        } catch (IOException | InterruptedException ex) {
            throw new KafkaStartingException("Error when execInContainer, ", ex);
        }
    }

    private static class SingletonHolder {

        private static final KafkaTestcontainerFactory INSTANCE = new KafkaTestcontainerFactory();

    }
}

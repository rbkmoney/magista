package com.rbkmoney.magista.config;

import com.rbkmoney.magista.exception.KafkaStartingException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
        Startables.deepStart(Stream.of(KAFKA_CONTAINER))
                .join();
        assertThat(KAFKA_CONTAINER.isRunning()).isTrue();
        createTopics("mg-invoice-100-2", "mg-events-invoice-template", "pm-events-payout");
        String topicCommand = "/usr/bin/kafka-topics --bootstrap-server=localhost:9092 --list";
        String stdout = null;
        try {
            stdout = KAFKA_CONTAINER.execInContainer("/bin/sh", "-c", topicCommand)
                    .getStdout();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertThat(stdout).contains("mg-events-invoice-template");
    }

    private static void createTopics(String... topics) {
        var newTopics = Stream.of(topics)
                .map(topic -> new NewTopic(topic, 1, (short) 1))
                .collect(Collectors.toList());
        try (var admin = createAdminClient()) {
            CreateTopicsResult topicsResult = admin.createTopics(newTopics);
            long countCreatedTopics = topicsResult.values().entrySet().stream().map(entry -> {
                try {
                    return entry.getValue().get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new KafkaStartingException("Error when topic creating, ", ex);
                }
            }).count();
            assertThat(newTopics.size()).isEqualTo(countCreatedTopics);
        }
    }

    private static AdminClient createAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        return AdminClient.create(properties);
    }
}

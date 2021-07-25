package com.rbkmoney.magista.config.testcontainers.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static com.rbkmoney.magista.config.testcontainers.util.SpringApplicationPropertiesLoader.loadStringValueFromSpringApplicationPropertiesFile;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaTestcontainerFactory {

    private static final String KAFKA_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String TAG_PROPERTY = "testcontainers.kafka.tag";

    private KafkaContainer kafkaContainer;

    public static KafkaContainer container() {
        return instance().create();
    }

    public static KafkaContainer singletonContainer() {
        return instance().getOrCreateSingletonContainer();
    }

    private static KafkaTestcontainerFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    @Synchronized
    private KafkaContainer getOrCreateSingletonContainer() {
        if (kafkaContainer != null) {
            return kafkaContainer;
        }
        kafkaContainer = create();
        return kafkaContainer;
    }

    private KafkaContainer create() {
        return new KafkaContainer(
                DockerImageName
                        .parse(KAFKA_IMAGE_NAME)
                        .withTag(loadStringValueFromSpringApplicationPropertiesFile(TAG_PROPERTY)))
                .withEmbeddedZookeeper();
    }

    private static class SingletonHolder {

        private static final KafkaTestcontainerFactory INSTANCE = new KafkaTestcontainerFactory();

    }
}

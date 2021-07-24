package com.rbkmoney.magista.config.testcontainer;

import lombok.Builder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Builder
public class SingletonPostgresqlTestcontainer {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "11.4";
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName
                    .parse(POSTGRESQL_IMAGE_NAME)
                    .withTag(POSTGRESQL_VERSION));

    static {
        startPostgresqlContainer();
    }

    private static void startPostgresqlContainer() {
        Startables.deepStart(Stream.of(POSTGRESQL_CONTAINER))
                .join();
        assertThat(POSTGRESQL_CONTAINER.isRunning())
                .isTrue();
    }
}

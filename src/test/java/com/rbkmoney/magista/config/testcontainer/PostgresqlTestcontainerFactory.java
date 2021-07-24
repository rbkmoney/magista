package com.rbkmoney.magista.config.testcontainer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgresqlTestcontainerFactory {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "11.4";

    private PostgreSQLContainer<?> postgreSqlContainer;

    public static PostgreSQLContainer<?> container() {
        return instance().getOrInitAndStartContainer();
    }

    private static PostgresqlTestcontainerFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    @Synchronized
    private PostgreSQLContainer<?> getOrInitAndStartContainer() {
        if (postgreSqlContainer != null) {
            return postgreSqlContainer;
        }
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
                DockerImageName
                        .parse(POSTGRESQL_IMAGE_NAME)
                        .withTag(POSTGRESQL_VERSION));
        startContainer(container);
        postgreSqlContainer = container;
        return postgreSqlContainer;
    }

    private void startContainer(PostgreSQLContainer<?> container) {
        Startables.deepStart(Stream.of(container))
                .join();
        assertThat(container.isRunning())
                .isTrue();
    }

    private static class SingletonHolder {

        private static final PostgresqlTestcontainerFactory INSTANCE = new PostgresqlTestcontainerFactory();

    }
}

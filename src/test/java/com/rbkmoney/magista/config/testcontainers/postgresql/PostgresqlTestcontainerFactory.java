package com.rbkmoney.magista.config.testcontainers.postgresql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static com.rbkmoney.magista.config.testcontainers.util.SpringApplicationPropertiesLoader.loadStringValueFromSpringApplicationPropertiesFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgresqlTestcontainerFactory {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String TAG_PROPERTY = "testcontainers.postgresql.tag";

    private PostgreSQLContainer<?> postgreSqlContainer;

    public static PostgreSQLContainer<?> container() {
        return instance().create();
    }

    public static PostgreSQLContainer<?> singletonContainer() {
        return instance().getOrCreateSingletonContainer();
    }

    private static PostgresqlTestcontainerFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    @Synchronized
    private PostgreSQLContainer<?> getOrCreateSingletonContainer() {
        if (postgreSqlContainer != null) {
            return postgreSqlContainer;
        }
        postgreSqlContainer = create();
        return postgreSqlContainer;
    }

    private PostgreSQLContainer<?> create() {
        return new PostgreSQLContainer<>(
                DockerImageName
                        .parse(POSTGRESQL_IMAGE_NAME)
                        .withTag(loadStringValueFromSpringApplicationPropertiesFile(TAG_PROPERTY)));
    }

    private static class SingletonHolder {

        private static final PostgresqlTestcontainerFactory INSTANCE = new PostgresqlTestcontainerFactory();

    }
}

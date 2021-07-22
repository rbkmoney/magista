package com.rbkmoney.magista.config;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

public abstract class AbstractDbSingletonContainerConfig {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "11.4";

    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName
                    .parse(POSTGRESQL_IMAGE_NAME)
                    .withTag(POSTGRESQL_VERSION));

    static {
        Startables.deepStart(Stream.of(POSTGRESQL_CONTAINER))
                .join();
    }
}

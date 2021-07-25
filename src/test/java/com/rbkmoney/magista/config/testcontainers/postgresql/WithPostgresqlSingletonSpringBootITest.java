package com.rbkmoney.magista.config.testcontainers.postgresql;

import com.rbkmoney.magista.config.testcontainers.spring.boot.test.context.DefaultSpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@DefaultSpringBootTest
public @interface WithPostgresqlSingletonSpringBootITest {
}

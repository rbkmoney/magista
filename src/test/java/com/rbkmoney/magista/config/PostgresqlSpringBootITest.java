package com.rbkmoney.magista.config;

import com.rbkmoney.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;
import com.rbkmoney.testcontainers.annotations.spring.boot.test.context.DefaultSpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@DefaultSpringBootTest
public @interface PostgresqlSpringBootITest {
}

package com.rbkmoney.magista.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DaoConfig {

    @Bean
    @Primary
    @ConfigurationProperties("datasource.master")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("datasource.master.configuration")
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("datasource.slave")
    public DataSourceProperties slaveDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("datasource.slave.configuration")
    public HikariDataSource slaveDataSource(DataSourceProperties slaveDataSourceProperties) {
        return slaveDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

}

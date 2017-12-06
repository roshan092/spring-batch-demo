package com.roshan092.springbatchdemo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "hikari.datasource.spring_batch")
public class DataSourceConfiguration extends HikariConfig {

    @Bean(name = "batchDataSource")
    public DataSource batchDataSource() {
        return new HikariDataSource(this);
    }

}

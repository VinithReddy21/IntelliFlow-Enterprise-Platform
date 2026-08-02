package com.intelliflow.common.performance.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Production HikariCP Connection Pool Optimization.
 * 
 * Tunes maximum pool size, idle timeouts, and connection leak detection.
 */
@Slf4j
@Configuration
public class HikariPoolConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName(properties.getDriverClassName());

        // HikariCP Production Tuning
        config.setMaximumPoolSize(30);
        config.setMinimumIdle(10);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000);       // 10 minutes
        config.setMaxLifetime(1800000);      // 30 minutes
        config.setLeakDetectionThreshold(20000); // 20 seconds

        // PostgreSQL Optimization Prepared Statements
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        log.info("Initialized Production HikariCP DataSource Pool [Max: 30, Min Idle: 10, LeakDetection: 20s]");
        return new HikariDataSource(config);
    }
}

package com.agrocesar.config;

import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbiConfig {

    @Bean
    @ConditionalOnMissingBean(Jdbi.class)
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource);
    }
}
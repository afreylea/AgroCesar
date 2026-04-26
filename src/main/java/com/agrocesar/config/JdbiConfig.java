package com.agrocesar.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!nobd") 
@ConditionalOnMissingBean
public class JdbiConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource)
                   .installPlugin(new SqlObjectPlugin());
    }
}
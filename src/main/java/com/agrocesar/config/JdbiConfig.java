package com.agrocesar.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.repository.UsuarioRepository;
import com.agrocesar.model.Municipio;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

@Configuration
public class JdbiConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @ConditionalOnMissingBean(Jdbi.class)
    public Jdbi jdbi() {
        javax.sql.DataSource dataSource = DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName("oracle.jdbc.OracleDriver")
            .build();
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(BeanMapper.factory(Municipio.class));
        return jdbi;
    }

    @Bean
    public UsuarioRepository usuarioRepository(Jdbi jdbi) {
        return jdbi.onDemand(UsuarioRepository.class);
    }

    @Bean
    public MunicipioRepository municipioRepository (Jdbi jdbi){
        return jdbi.onDemand(MunicipioRepository.class);
    }
}
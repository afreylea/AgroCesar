package com.agrocesar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Profile("nobd")   // Solo activo con --spring.profiles.active=nobd
public class TestUsersConfig {

    @Bean
    public UserDetailsService userDetailsService(
            org.springframework.security.crypto.password.PasswordEncoder encoder) {

        return new InMemoryUserDetailsManager(
            User.builder()
                .username("agricultor@test.com")
                .password(encoder.encode("1234"))
                .roles("AGRICULTOR")
                .build(),
            User.builder()
                .username("admin@test.com")
                .password(encoder.encode("1234"))
                .roles("ADMIN")
                .build()
        );
    }
}
package com.agrocesar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${webclient.open-meteo.base-url}")
    private String baseUrl;

    @Value("${webclient.open-meteo.timeout-seconds}")
    private int timeoutSeconds;

    @Bean
    public WebClient openMeteoWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
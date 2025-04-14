package com.pm.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean(name = "accountsWebClient")
    public WebClient accountsWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:4004")
                .build();
    }

    @Bean(name = "patientWebClient")
    public WebClient patientWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:4004")
                .build();
    }
}

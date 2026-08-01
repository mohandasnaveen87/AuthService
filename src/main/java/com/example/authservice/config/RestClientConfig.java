package com.example.authservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    //@LoadBalanced // This connects your RestClient directly to the Eureka directory
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
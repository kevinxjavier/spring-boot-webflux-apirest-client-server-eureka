package com.kevinpina.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${config.base.endpoint}")
    private String url;

    @Bean
    @LoadBalanced // Enabling Load Balancer in Eureka Server. Using by default "Ribbon Load Balancer" from Spring Cloud
    public WebClient.Builder registerWebClient() {
        return WebClient.builder().baseUrl(url);
    }

}

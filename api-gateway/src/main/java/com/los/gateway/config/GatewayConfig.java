package com.los.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Customer Service Routes
                .route("customer-service", r -> r
                        .path("/api/customers/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("customerServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/customers")))
                        .uri("http://localhost:8081"))
                
                // Loan Service Routes
                .route("loan-service", r -> r
                        .path("/api/loans/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("loanServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/loans")))
                        .uri("http://localhost:8082"))
                
                // Credit Engine Service Routes
                .route("credit-engine-service", r -> r
                        .path("/api/credit/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("creditEngineCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/credit")))
                        .uri("http://localhost:8083"))
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8084"))
                
                .build();
    }
}

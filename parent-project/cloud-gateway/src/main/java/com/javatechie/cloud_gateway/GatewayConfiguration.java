package com.javatechie.cloud_gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class GatewayConfiguration {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> slowCusomtizer() {
        return factory -> {
            factory.configure(builder -> builder
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()), "slow", "slowflux");
            factory.addCircuitBreakerCustomizer(circuitBreaker -> circuitBreaker.getEventPublisher()
                    .onError((errorEvent)->System.out.println("Error")).onSuccess((successEvent)->System.out.println("Success")), "normalflux");
        };
    }

}

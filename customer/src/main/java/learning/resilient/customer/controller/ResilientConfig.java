package learning.resilient.customer.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import learning.resilient.customer.exception.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResilientConfig {

//  @Bean
//  public CircuitBreakerRegistry circuitBreakerRegistry() {
//    CircuitBreakerConfig circuitBreakerConfig =
//        CircuitBreakerConfig.custom()
//            .failureRateThreshold(50)
//            .waitDurationInOpenState(Duration.ofMillis(10000))
//            .minimumNumberOfCalls(10)
//            .permittedNumberOfCallsInHalfOpenState(3)
//            .ignoreExceptions(BusinessException.class)
//            .slidingWindowSize(5)
//            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
//            .build();
//
//    return CircuitBreakerRegistry.of(circuitBreakerConfig);
//  }
}

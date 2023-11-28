package learning.resilient.customer.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Endpoint(id = "circuit-breaker-health")
public class CircuitBreakerHealthEndpoint {

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Autowired
  public CircuitBreakerHealthEndpoint(CircuitBreakerRegistry circuitBreakerRegistry) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @ReadOperation
  public List<CircuitBreakerHealth> getHealth() {
    return circuitBreakerRegistry.getAllCircuitBreakers()
        .map(e -> new CircuitBreakerHealth(e.getName(), e.getState().name(), e.getMetrics()))
        .collect(Collectors.toList());
  }

  @Data
  @AllArgsConstructor
  public static class CircuitBreakerHealth {
    private final String name;
    private final String state;
    private final CircuitBreaker.Metrics metrics;
  }
}

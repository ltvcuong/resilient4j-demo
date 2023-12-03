package learning.resilient.customer.controller;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.bulkhead.event.BulkheadEvent;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResilientConfig {
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  private final ThreadPoolBulkheadRegistry bulkheadRegistry;

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

  @PostConstruct
  public void registerEventConsumers() {
    registerCBEventConsumers();
    registerBulkheadEventConsumers();
  }

  private void registerCBEventConsumers() {
    CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.getAllCircuitBreakers().iterator().next();

    EventConsumer<CircuitBreakerEvent> circuitBreakerEventConsumer =
        event -> {
          switch (event.getEventType()) {
            case STATE_TRANSITION:
              CircuitBreakerOnStateTransitionEvent stateTransitionEvent =
                  (CircuitBreakerOnStateTransitionEvent) event;
              log.info(
                  "CB state transition: CB name = {}, from = {}, to = {}, time = {}",
                  stateTransitionEvent.getCircuitBreakerName(),
                  stateTransitionEvent.getStateTransition().getFromState(),
                  stateTransitionEvent.getStateTransition().getToState(),
                  event.getCreationTime());
              break;
            default:
              log.info(
                  "CB event: CB name = {}, event = {}, eventTs = {}",
                  event.getCircuitBreakerName(),
                  event.getEventType(),
                  event.getCreationTime());
          }
        };

    circuitBreaker.getEventPublisher().onEvent(circuitBreakerEventConsumer);
  }

  private void registerBulkheadEventConsumers() {
    ThreadPoolBulkhead bulkhead = bulkheadRegistry.getAllBulkheads().iterator().next();

    EventConsumer<BulkheadEvent> bulkheadEventEventConsumer =
        event -> {
          log.info(
              "Bulkhead event: bulkhead name = {}, event = {}, eventTs = {}",
              event.getBulkheadName(),
              event.getEventType(),
              event.getCreationTime());
        };

    bulkhead.getEventPublisher().onEvent(bulkheadEventEventConsumer);
  }
}

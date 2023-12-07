package learning.resilient.customer.controller;

import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.bulkhead.event.BulkheadEvent;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.timelimiter.event.TimeLimiterEvent;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResilientConfig {
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  private final ThreadPoolBulkheadRegistry bulkheadRegistry;

  private final TimeLimiterRegistry timeLimiterRegistry;

//    @Bean
//    public BulkheadRegistry bulkheadRegistry() {
//      BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
//              .maxConcurrentCalls(3)
//              .maxWaitDuration(Duration.ofMillis(500))
//              .build();
//
//      return BulkheadRegistry.of(bulkheadConfig);
//    }
//
//  @Bean
//  public ThreadPoolBulkheadRegistry threadPollBulkheadRegistry() {
//    ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
//            .maxThreadPoolSize(10)
//            .coreThreadPoolSize(2)
//            .queueCapacity(20)
//            .build();
//
//    return BulkheadRegistry.of(config);
//  }

  @PostConstruct
  public void registerEventConsumers() {
    registerCBEventConsumers();
    registerBulkheadEventConsumers();
    registerTimeLimiterEventConsumers();
  }

  private void registerTimeLimiterEventConsumers() {
    var iter = timeLimiterRegistry.getAllTimeLimiters().iterator();
    while (iter.hasNext()) {
      var timelimiter = iter.next();
      EventConsumer<TimeLimiterEvent> timeLimiterEventEventConsumer =
          event -> {
            log.info(
                "Time limiter event: time limiter name = {}, event = {}, eventTs = {}",
                event.getTimeLimiterName(),
                event.getEventType(),
                event.getCreationTime());
          };

      timelimiter.getEventPublisher().onEvent(timeLimiterEventEventConsumer);
    }
  }

  private void registerCBEventConsumers() {
    var iter = circuitBreakerRegistry.getAllCircuitBreakers().iterator();

    while (iter.hasNext()) {
      CircuitBreaker circuitBreaker = iter.next();
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
  }

  private void registerBulkheadEventConsumers() {
    var iter = bulkheadRegistry.getAllBulkheads().iterator();

    while (iter.hasNext()) {
      var bulkhead = iter.next();
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
}

package learning.resilient.customer.service;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import learning.resilient.customer.extservice.Order;
import learning.resilient.customer.extservice.OrderServiceClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

  private final OrderServiceClient orderService;

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  private final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;

  private final BulkheadRegistry bulkheadRegistry;

  private final TimeLimiterRegistry timeLimiterRegistry;

  @Override
  public List<Customer> success() {
    return resilientCallOrderSvc(orderService::success);
  }

  @Override
  public List<Customer> businessError() {
    return resilientCallOrderSvc(orderService::businessError);
  }

  @Override
  public List<Customer> technicalError() {
    return resilientCallOrderSvc(orderService::technicalError);
  }

  @Override
  public List<Customer> slow() {
    return resilientCallOrderSvc(orderService::slow);
  }

  @Override
  public List<Customer> resilentSlowWithAnnotation() {
    List<Customer> customers = new ArrayList<>();
    customers.add(new Customer("1", "CK", orderService.resilientSlowWithAnnotation()));
    return customers;
  }

  private List<Customer> resilientCallOrderSvc(Supplier<List<Order>> orderSupplier) {
    List<Customer> customers = new ArrayList<>();

    var circuitBreaker = circuitBreakerRegistry.circuitBreaker(OrderServiceClient.SERVICE);
    var bulkhead = threadPoolBulkheadRegistry.bulkhead(OrderServiceClient.SERVICE);
    var timelimiter = timeLimiterRegistry.timeLimiter(OrderServiceClient.SERVICE);

    var decorateCompletionStage =
        Decorators.ofSupplier(orderSupplier)
            .withThreadPoolBulkhead(bulkhead)
            .withTimeLimiter(timelimiter, Executors.newScheduledThreadPool(3))
            .withCircuitBreaker(circuitBreaker)
            .decorate();
    decorateCompletionStage
        .get()
        .thenAccept(
            orders -> {
              customers.add(new Customer("1", "John Doe", orders));
            })
        .toCompletableFuture()
        .join();
    return customers;
  }
}

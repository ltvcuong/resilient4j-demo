package learning.resilient.customer.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.vavr.control.Try;
import learning.resilient.customer.exception.ErrorResponse;
import learning.resilient.customer.exception.TechnicalException;
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

  private List<Customer> resilientCallOrderSvc(Supplier<List<Order>> orderSupplier) {
    var circuitBreaker = circuitBreakerRegistry.circuitBreaker(OrderServiceClient.SERVICE);
    Supplier<List<Order>> decoratedSupplier =
        CircuitBreaker.decorateSupplier(circuitBreaker, orderSupplier);
    var decoratedTry =
        Try.ofSupplier(decoratedSupplier)
            .onFailure(
                t -> {
                  if (t instanceof CallNotPermittedException) {
                    throw new TechnicalException(
                        ErrorResponse.builder()
                            .code("circuitbreaker_open")
                            .description(t.getMessage())
                            .build());
                  }
                });

    List<Customer> customers = new ArrayList<>();
    customers.add(new Customer("1", "John Doe", decoratedTry.get()));
    return customers;
  }
}

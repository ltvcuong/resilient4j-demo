package learning.resilient.customer.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import learning.resilient.customer.extservice.Order;
import learning.resilient.customer.extservice.OrderServiceClient;
import learning.resilient.customer.extservice.OrderServiceFeignClient;
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
    return getResult(orderService::success);
  }

  @Override
  public List<Customer> businessError() {
    return getResult(orderService::businessError);
  }

  @Override
  public List<Customer> technicalError() {
    return getResult(orderService::technicalError);
  }

  private List<Customer> getResult(Supplier<List<Order>> orderSupplier) {
    //        var circuitBreaker =
    // circuitBreakerRegistry.circuitBreaker(OrderServiceClient.SERVICE);
    //        Supplier<List<Order>> decoratedSupplier =
    //            CircuitBreaker.decorateSupplier(circuitBreaker, orderSupplier);

    List<Customer> customers = new ArrayList<>();
    customers.add(new Customer("1", "John Doe", orderSupplier.get()));
    return customers;
  }
}

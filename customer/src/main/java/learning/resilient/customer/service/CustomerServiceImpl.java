package learning.resilient.customer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import learning.resilient.customer.extservice.Order;
import learning.resilient.customer.extservice.OrderServiceFeignClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

  private final OrderServiceFeignClient orderServiceFeignClient;

  @Value("${resilience4j.decorators.enabled:false}")
  private String decoratorEnabled;

  @Override
  public List<Customer> success() {
    return getResult(orderServiceFeignClient::success);
  }

  @Override
  public List<Customer> businessError() {
    return getResult(orderServiceFeignClient::businessError);
  }

  @Override
  public List<Customer> technicalError() {
    return getResult(orderServiceFeignClient::technicalError);
  }

  @Override
  public List<Customer> slow() {
    orderServiceFeignClient.slow();
    return getResult(orderServiceFeignClient::slow);
  }

  @Override
  public List<Customer> resilentSlowWithAnnotation() {
    List<Customer> customers = new ArrayList<>();
    customers.add(new Customer("1", "CK", orderServiceFeignClient.resilientSlowWithAnnotation()));
    return customers;
  }

  private List<Customer> getResult(Supplier<List<Order>> orderSupplier) {
    var customers = new ArrayList<Customer>();
    customers.add(new Customer("1", "John Doe", orderSupplier.get()));
    return customers;
  }
}

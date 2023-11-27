package learning.resilient.customer.service;

import java.util.List;

public interface CustomerService {
  List<Customer> success();

  List<Customer> businessError();

  List<Customer> technicalError();
}

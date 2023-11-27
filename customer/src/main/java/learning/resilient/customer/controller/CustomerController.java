package learning.resilient.customer.controller;

import java.util.List;
import javax.ws.rs.Produces;
import learning.resilient.customer.service.Customer;
import learning.resilient.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping("/success")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Customer> success() {
    return customerService.success();
  }

  @GetMapping("/business-error")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Customer> businessError() {
    return customerService.businessError();
  }

  @GetMapping("/tech-error")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Customer> technicalError() {
    return customerService.technicalError();
  }
}

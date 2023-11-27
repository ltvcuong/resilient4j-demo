package learning.resilient.order.controller;

import learning.resilient.order.exception.BusinessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
  @GetMapping("/success")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Order> success() {
    // Generate sample data
    List<Order> orders = new ArrayList<>();
    orders.add(new Order("1", "customer1", 29.99));
    orders.add(new Order("2", "customer2", 49.99));
    orders.add(new Order("3", "customer3", 19.99));
    return orders;
  }

  @GetMapping("/business-error")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Order> businessError() throws BusinessException {
    throw new BusinessException();
  }

  @GetMapping("/tech-error")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public List<Order> techError() {
    throw new RuntimeException();
  }
}

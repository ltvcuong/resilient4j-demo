package learning.resilient.customer.extservice;

import feign.RequestLine;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// @CircuitBreaker(name = OrderServiceClient.SERVICE)
public interface OrderServiceFeignClient {
  String SERVICE = "order";

  @RequestLine("GET /v1/success")
  List<Order> success();

  @RequestLine("GET /v1/business-error")
  List<Order> businessError();

  @RequestLine("GET /v1/tech-error")
  List<Order> technicalError();
}

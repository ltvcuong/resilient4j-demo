package learning.resilient.customer.extservice;

import feign.RequestLine;
import java.util.List;

public interface OrderServiceFeignClient {
  String SERVICE = "order";

  @RequestLine("GET /v1/success")
  List<Order> success();

  @RequestLine("GET /v1/business-error")
  List<Order> businessError();

  @RequestLine("GET /v1/tech-error")
  List<Order> technicalError();

  @RequestLine("GET /v1/slow")
  List<Order> slow();
}

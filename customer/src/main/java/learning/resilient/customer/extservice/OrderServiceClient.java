package learning.resilient.customer.extservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//@CircuitBreaker(name = OrderServiceClient.SERVICE)
public interface OrderServiceClient {
  String SERVICE = "order";

  @GET
  @Path("/v1/orders/success")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> success();

  @GET
  @Path("/v1/orders/business-error")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> businessError();

//  @CircuitBreaker(name = OrderServiceClient.SERVICE)
  @GET
  @Path("/v1/orders/tech-error")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> technicalError();
}

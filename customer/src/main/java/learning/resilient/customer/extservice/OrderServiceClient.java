package learning.resilient.customer.extservice;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

  @GET
  @Path("/v1/orders/tech-error")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> technicalError();

  @GET
  @Path("/v1/orders/slow")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> slow();
  
  @GET
  @Path("/v1/orders/slow")
  @Produces(MediaType.APPLICATION_JSON)
  List<Order> resilientSlowWithAnnotation();
}

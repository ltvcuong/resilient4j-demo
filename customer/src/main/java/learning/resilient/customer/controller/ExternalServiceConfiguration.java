package learning.resilient.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import learning.resilient.customer.extservice.OrderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalServiceConfiguration {
  @Autowired FeignErrorDecoderConfig.OrderErrorDecoder orderErrorDecoder;

  @Autowired CircuitBreakerRegistry circuitBreakerRegistry;

  @Autowired BulkheadRegistry bulkheadRegistry;

  @Bean
  OrderServiceClient orderServiceClient() {
    var url = "http://localhost:9081";
    return Feign.builder()
        .decoder(new Decoder.Default())
        .contract(new JAXRSContract())
        .decoder(new JacksonDecoder(new ObjectMapper()))
        .encoder(new JacksonEncoder(new ObjectMapper()))
        .errorDecoder(orderErrorDecoder)
        .target(OrderServiceClient.class, url);
  }
}

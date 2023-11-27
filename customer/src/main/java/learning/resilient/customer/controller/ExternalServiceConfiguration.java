package learning.resilient.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import learning.resilient.customer.extservice.OrderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalServiceConfiguration {
  @Autowired FeignErrorDecoderConfig.ErrorDecoder errorDecoder;

  @Autowired CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  OrderServiceClient orderServiceClient() {
    var url = "http://localhost:9081";
    // For decorating a feign interface
    CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.circuitBreaker(OrderServiceClient.SERVICE);
    FeignDecorators decorators =
        FeignDecorators.builder().withCircuitBreaker(circuitBreaker).build();
    return Resilience4jFeign.builder(decorators)
        .decoder(new Decoder.Default())
        .contract(new JAXRSContract())
        .decoder(new JacksonDecoder(new ObjectMapper()))
        .encoder(new JacksonEncoder(new ObjectMapper()))
        .errorDecoder(errorDecoder)
        .target(OrderServiceClient.class, url);
  }

//  @Bean
//  OrderServiceFeignClient orderServiceFeignClient() {
//    var url = "http://localhost:9081";
//    // For decorating a feign interface
//    CircuitBreaker circuitBreaker =
//        circuitBreakerRegistry.circuitBreaker(OrderServiceClient.SERVICE);
//    FeignDecorators decorators =
//        FeignDecorators.builder().withCircuitBreaker(circuitBreaker).build();
//    return Resilience4jFeign.builder(decorators)
//        .decoder(new Decoder.Default())
//        .decoder(new JacksonDecoder(new ObjectMapper()))
//        .encoder(new JacksonEncoder(new ObjectMapper()))
//        .errorDecoder(errorDecoder)
//        .target(OrderServiceFeignClient.class, url);
//  }
}

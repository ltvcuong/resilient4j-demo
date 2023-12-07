package learning.resilient.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Request;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import java.util.concurrent.TimeUnit;
import learning.resilient.customer.extservice.OrderServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ExternalServiceConfiguration {
  @Autowired FeignErrorDecoderConfig.OrderErrorDecoder orderErrorDecoder;

  private static final long CONNECT_TIMEOUT = 1000;
  private static final long READ_TIMEOUT = 2000;

  @Bean
  OrderServiceClient orderServiceClient() {
    var url = "http://localhost:9081";
    return Feign.builder()
        .decoder(new Decoder.Default())
        .contract(new JAXRSContract())
        .options(
            new Request.Options(
                CONNECT_TIMEOUT, TimeUnit.MILLISECONDS, READ_TIMEOUT, TimeUnit.MILLISECONDS, true))
        .decoder(new JacksonDecoder(new ObjectMapper()))
        .encoder(new JacksonEncoder(new ObjectMapper()))
        .errorDecoder(orderErrorDecoder)
        .logLevel(feign.Logger.Level.FULL)
        .target(OrderServiceClient.class, url);
  }
}

package learning.resilient.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import learning.resilient.customer.exception.BusinessException;
import learning.resilient.customer.exception.ErrorResponse;
import learning.resilient.customer.exception.TechnicalException;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class FeignErrorDecoderConfig {
  @Bean
  public ErrorDecoder errorDecoder() {
    return new ErrorDecoder();
  }

  static class ErrorDecoder implements feign.codec.ErrorDecoder {

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
      final int status = response.status();
      if (status >= 400 && status <= 499) {
        var errorJson = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
        var error = new ObjectMapper().readValue(errorJson, ErrorResponse.class);
        return new BusinessException(error);
      }

      if (status >= 500 && status <= 599) {
        ErrorResponse error;
        try {
          var errorJson = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
          error = new ObjectMapper().readValue(errorJson, ErrorResponse.class);
        } catch (Exception e) {
          // nope
          error = new ErrorResponse();
        }
        return new TechnicalException(error);
      }
      return new Default().decode(methodKey, response);
    }
  }
}

package learning.resilient.customer.exception;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestController
@Slf4j
@RequiredArgsConstructor
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler({TechnicalException.class})
  public ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex) {
    var error =
        ErrorResponse.builder()
            .code("technical error")
            .description("something's wrong with system.")
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({BusinessException.class})
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    var error =
        ErrorResponse.builder()
            .code(
                (Objects.nonNull(ex.getErrorResponse())
                        && Objects.nonNull(ex.getErrorResponse().getCode()))
                    ? ex.getErrorResponse().getCode()
                    : "business_exception")
            .description("Some biz error")
            .build();
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler({CallNotPermittedException.class})
  public ResponseEntity<ErrorResponse> handleCallNotPermittedException(
      CallNotPermittedException ex) {
    var error =
        ErrorResponse.builder()
            .code("CallNotPermittedException")
            .description("Some tech error")
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({BulkheadFullException.class})
  public ResponseEntity<ErrorResponse> handleBulkheadFullException(BulkheadFullException ex) {
    var error =
        ErrorResponse.builder()
            .code("BulkheadFullException")
            .description("Some tech error")
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({TimeoutException.class})
  public ResponseEntity<ErrorResponse> handleBulkheadFullException(TimeoutException ex) {
    var error =
        ErrorResponse.builder().code("TimeoutException").description("Some tech error").build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

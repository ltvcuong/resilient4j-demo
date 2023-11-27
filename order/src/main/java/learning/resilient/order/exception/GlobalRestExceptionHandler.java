package learning.resilient.order.exception;

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

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestController
@Slf4j
@RequiredArgsConstructor
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({BusinessException.class})
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    var error =
        ErrorResponse.builder().code("business_exception").description("Some biz error").build();
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> handleTechnicalException(Exception ex) {
    var error =
        ErrorResponse.builder()
            .code("technical error")
            .description("something's wrong with system.")
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

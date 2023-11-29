package learning.resilient.customer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TechnicalException extends RuntimeException {
  private final ErrorResponse errorResponse;

  public static TechnicalException defaultException() {
    return new TechnicalException(
        ErrorResponse.builder()
            .code("internal_server_error")
            .description("We're experiencing technical issues.")
            .build());
  }
}

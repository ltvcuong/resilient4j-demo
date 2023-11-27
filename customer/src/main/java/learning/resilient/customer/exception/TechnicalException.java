package learning.resilient.customer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TechnicalException extends Exception {
  private final ErrorResponse errorResponse;
}

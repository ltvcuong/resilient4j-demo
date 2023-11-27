package learning.resilient.customer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BusinessException extends Exception {
  private final ErrorResponse errorResponse;
}

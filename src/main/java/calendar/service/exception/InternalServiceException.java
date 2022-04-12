package calendar.service.exception;

public class InternalServiceException extends RuntimeException {
  public InternalServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}

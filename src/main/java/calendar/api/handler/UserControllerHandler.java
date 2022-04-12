package calendar.api.handler;

import calendar.service.exception.AlreadyExistsException;
import calendar.service.exception.InternalServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class UserControllerHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = AlreadyExistsException.class)
  protected ResponseEntity<Object> handleConflict(AlreadyExistsException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = InternalServiceException.class)
  protected ResponseEntity<Object> handleInternalException(InternalServiceException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}

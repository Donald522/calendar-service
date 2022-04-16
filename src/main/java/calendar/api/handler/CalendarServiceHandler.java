package calendar.api.handler;

import calendar.service.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CalendarServiceHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = AlreadyExistsException.class)
  protected ResponseEntity<Object> handleConflict(AlreadyExistsException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = InternalServiceException.class)
  protected ResponseEntity<Object> handleInternalException(InternalServiceException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = BadRequestException.class)
  protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = AuthException.class)
  protected ResponseEntity<Object> handleAuthException(AuthException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(value = NotFoundException.class)
  protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }
}

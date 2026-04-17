package com.jorgeturbica.prices.infrastructure.rest.exception;

import com.jorgeturbica.prices.domain.exception.PriceNotFoundException;
import com.jorgeturbica.prices.infrastructure.rest.generated.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(PriceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(buildError(HttpStatus.BAD_REQUEST.value(),"Missing required parameter: " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(buildError(HttpStatus.BAD_REQUEST.value(), "Invalid value for parameter '%s': %s".formatted(ex.getName(), ex.getValue())));
    }

    private ErrorResponse buildError(int status, String message) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(status);
        error.setMessage(message);
        return error;
    }
}

package com.pyrosandro.common.error;

import com.pyrosandro.common.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected final boolean printStackTrace;

    protected final MessageSource messageSource;

    public GlobalExceptionHandler(@Value("${common.printstacktrace:false}") boolean printStackTrace, MessageSource messageSource) {
        this.printStackTrace = printStackTrace;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("ConstraintViolation Validation error occurred", ex);

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST, messageSource.getMessage(String.valueOf(ErrorConstants.CONSTRAINT_VALIDATION_ERROR.getCode()), null, Locale.getDefault()));
        for (ConstraintViolation constraintViolation : ex.getConstraintViolations()) {
            errorDTO.addValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        if (printStackTrace) {
            errorDTO.setStackTrace(ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("MethodArgumentNotValid Validation error occurred", ex);

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST, messageSource.getMessage(String.valueOf(ErrorConstants.CONSTRAINT_VALIDATION_ERROR.getCode()), null, Locale.getDefault()));
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorDTO.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        if (printStackTrace) {
            errorDTO.setStackTrace(ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtRuntimeExceptions(RuntimeException ex, WebRequest request) {
        log.error("Unknown error occurred", ex);
        return buildErrorDTO(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtExceptions(Exception ex, WebRequest request) {
        log.error("Unknown error occurred", ex);
        return buildErrorDTO(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorDTO(ex, status, request);
    }

    protected ResponseEntity<Object> buildErrorDTO(Exception ex, String message, HttpStatus status, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(status, message);
        if (printStackTrace) {
            errorDTO.setStackTrace(ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.status(status).body(errorDTO);
    }

    protected ResponseEntity<Object> buildErrorDTO(Exception ex, HttpStatus status, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(status, ex.getMessage());
        if (printStackTrace) {
            errorDTO.setStackTrace(ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.status(status).body(errorDTO);
    }

}
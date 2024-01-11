package com.pyrosandro.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ErrorDTO {

    private HttpStatus httpStatus;
    private String message;
    private LocalDateTime dateTime;
    private List<ValidationError> validationErrors;
    private String stackTrace;

    public ErrorDTO() {
        dateTime = LocalDateTime.now();
    }

    public ErrorDTO(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        dateTime = LocalDateTime.now();
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message){
        if(Objects.isNull(validationErrors)){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(field, message));
    }

}
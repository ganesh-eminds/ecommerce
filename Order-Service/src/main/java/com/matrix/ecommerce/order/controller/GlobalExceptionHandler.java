package com.matrix.ecommerce.order.controller;

import com.matrix.ecommerce.dtos.dto.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.dto.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// handles all the exceptions that occur in the application
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // handles all the exceptions that occur in the application
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleAllExceptions(Exception ex) {
        log.info("Handling exception: {}", ex.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage(), "500", "Internal Server Error", "An unexpected error occurred");
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // handle out of stock exception
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionDto> handleValidationException(ValidationException ex) {
        log.info("Handling Validation exception: {}", ex.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage(), ex.getErrorCode(), ex.getErrorType(), ex.getErrorDescription());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
}

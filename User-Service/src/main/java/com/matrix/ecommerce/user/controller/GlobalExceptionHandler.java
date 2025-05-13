package com.matrix.ecommerce.user.controller;

import com.matrix.ecommerce.dtos.dto.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.dto.exception.UserNotFoundException;
import com.matrix.ecommerce.dtos.dto.dto.exception.ValidationException;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationException> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.info("In ResourceNotFoundException handling method", ex.getMessage());
        ValidationException errorResponse = new ValidationException("Resource Not Found", "404", "RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<OrderRequest> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.info("In UserNotFoundException handling method", ex.getMessage());
        UUID userId = (UUID) request.getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .exception(new ExceptionDto(
                        "User Not Found",
                        "404",
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage()
                ))
                .build();
        return new ResponseEntity<>(orderRequest, HttpStatus.NOT_FOUND);
//        return ResponseEntity.ok(userService.getUserOrders(id));
    }
}

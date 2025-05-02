package com.matrix.ecommerce.dtos.dto.exception;

// common exception class for all validation exceptions
// use ExceptionDTO to return error response
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String errorType;
    private String errorDescription;
    private ExceptionDto exceptionDto;

    public ValidationException(String message, String errorCode, String errorType, String errorDescription) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorDescription = errorDescription;
    }

    public ValidationException(ExceptionDto exceptionDto) {
        super(exceptionDto.getMessage());
        this.errorCode = exceptionDto.getErrorCode();
        this.errorType = exceptionDto.getErrorType();
        this.errorDescription = exceptionDto.getErrorDescription();
        this.exceptionDto = exceptionDto;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
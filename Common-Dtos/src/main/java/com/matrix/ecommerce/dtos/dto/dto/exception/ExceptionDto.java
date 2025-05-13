package com.matrix.ecommerce.dtos.dto.dto.exception;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {
    private String message;
    private String errorCode;
    private String errorType;
    private String errorDescription;

    public ExceptionDto(String message, String errorCode, String errorType, String errorDescription) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorDescription = errorDescription;
    }

    public ExceptionDto(String message, String errorCode, String errorDescription) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}

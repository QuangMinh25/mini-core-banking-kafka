package com.minh.bankingcore.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),
    ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST, "Account is inactive"),
    CURRENCY_MISMATCH(HttpStatus.BAD_REQUEST, "Currency mismatch"),
    DUPLICATE_ACCOUNT_NO(HttpStatus.CONFLICT, "Duplicate account number"),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "Invalid amount"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Insufficient balance"),
    SAME_ACCOUNT_TRANSFER(HttpStatus.BAD_REQUEST, "Source and destination accounts must be different"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

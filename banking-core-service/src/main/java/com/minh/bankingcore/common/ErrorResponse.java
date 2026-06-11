package com.minh.bankingcore.common;

public record ErrorResponse(boolean success, String code, String message) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(false, errorCode.name(), message);
    }
}

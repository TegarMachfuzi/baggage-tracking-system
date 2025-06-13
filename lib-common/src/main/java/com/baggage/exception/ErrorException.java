package com.baggage.exception;

public class ErrorException extends RuntimeException {
    private String errorCode;
    private String reason;

    public ErrorException(String errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

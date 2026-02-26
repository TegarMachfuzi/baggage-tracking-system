package com.passenger.exception;

public class DuplicatePassengerException extends RuntimeException {
    public DuplicatePassengerException(String message) {
        super(message);
    }
}

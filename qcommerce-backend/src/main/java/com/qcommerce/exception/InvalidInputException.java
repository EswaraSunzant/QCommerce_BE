package com.qcommerce.exception; // Updated package

/**
 * Custom exception for invalid input.
 */
public class InvalidInputException extends RuntimeException {
    private Object details;

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}

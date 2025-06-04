package com.qcommerce.exception; // Updated package

/**
 * Custom exception for cases where a user already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

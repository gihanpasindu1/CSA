package com.smartcampus.exception;

/**
 * Exception thrown when a reference to another resource (like roomId) is not found.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}

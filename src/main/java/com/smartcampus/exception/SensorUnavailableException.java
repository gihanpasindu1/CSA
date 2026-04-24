package com.smartcampus.exception;

/**
 * Exception thrown when a sensor is unavailable (e.g., MAINTENANCE) and cannot accept readings.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}

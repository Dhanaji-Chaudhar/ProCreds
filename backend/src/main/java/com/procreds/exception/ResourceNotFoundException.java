package com.procreds.exception;

/**
 * Exception thrown when a requested resource is not found
 * 
 * This exception is typically thrown by service classes when attempting
 * to retrieve, update, or delete a resource that doesn't exist in the database.
 * 
 * @author ProCreds Team
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message
     * 
     * @param message the detail message explaining the cause of the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


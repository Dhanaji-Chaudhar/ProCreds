package com.procreds.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 * 
 * This exception is typically thrown by service classes when attempting
 * to create a resource with a unique identifier that already exists in the database.
 * 
 * @author ProCreds Team
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new ResourceAlreadyExistsException with the specified detail message
     * 
     * @param message the detail message explaining the cause of the exception
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceAlreadyExistsException with the specified detail message and cause
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause the cause of the exception
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}


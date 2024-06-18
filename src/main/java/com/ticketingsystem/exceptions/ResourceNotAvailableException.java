package com.ticketingsystem.exceptions;

/**
 * Custom Exception used to indicate that content doesn't exist
 */
public class ResourceNotAvailableException extends RuntimeException {

    public ResourceNotAvailableException(String message) {
        super(message);
    }
}

package com.ticketingsystem.exceptions;

/**
 * Custom exception used for Bad requests
 */
public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
}

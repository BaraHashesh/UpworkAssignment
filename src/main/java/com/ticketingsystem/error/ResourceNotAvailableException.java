package com.ticketingsystem.error;

public class ResourceNotAvailableException extends RuntimeException {

    public ResourceNotAvailableException(String message) {
        super(message);
    }
}

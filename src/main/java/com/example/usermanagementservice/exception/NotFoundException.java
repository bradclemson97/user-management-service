package com.example.usermanagementservice.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * An exception that is thrown when a requested entity does not exist.
 */
public class NotFoundException extends ResponseStatusException {

    private static final HttpStatusCode CODE = NOT_FOUND;

    public NotFoundException(String message) { super(CODE, message); }
}

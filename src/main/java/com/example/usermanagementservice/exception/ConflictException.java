package com.example.usermanagementservice.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * An exception that is thrown when there is conflict between entity properties.
 */
public class ConflictException extends ResponseStatusException {

    private static final HttpStatusCode CODE = CONFLICT;

    public ConflictException(String message) { super(CODE, message); }
}

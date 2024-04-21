package com.example.whynotpc.utils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
@ResponseBody
public class ServiceExceptionHandler {
    public record Error(String message) {
    }

    /**
     * Handles IllegalArgumentException or IllegalStateException exceptions with a HTTP status code of BAD_REQUEST (400).
     *
     * @param ex the exception to handle
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(BAD_REQUEST)
    public void handleIllegalArgumentOrStateException(Exception ex) {
        System.out.println(ex.toString());
    }

    /**
     * Handles NoAuthenticationException or ExpiredJwtException exceptions with a HTTP status code of UNAUTHORIZED (401).
     *
     * @param ex the exception to handle
     */
    @ExceptionHandler({NoAuthenticationException.class, ExpiredJwtException.class})
    @ResponseStatus(UNAUTHORIZED)
    public void handleAuthenticationException(Exception ex) {
        System.out.println(ex.toString());
    }

    /**
     * Handles EntityNotFoundException exceptions with a HTTP status code of NOT_FOUND (404).
     *
     * @param ex the exception to handle
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public void handleEntityNotFoundException(EntityNotFoundException ex) {
        System.out.println(ex.toString());
    }

    /**
     * Handles DataIntegrityViolationException exceptions with a HTTP status code of CONFLICT (409).
     *
     * @param ex the exception to handle
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(CONFLICT)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.out.println(ex.toString());
    }

    /**
     * Handles RuntimeException exceptions with a HTTP status code of INTERNAL_SERVER_ERROR (500).
     *
     * @param ex the exception to handle
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public void handleRuntimeException(RuntimeException ex) {
        System.out.println(ex.toString());
    }
}

package com.example.authservice.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.authservice.RoleNotFoundException;
import com.example.authservice.ServiceUnavailableException;
import com.example.authservice.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // Sends 503 Status Code
    public ErrorResponse handleServiceUnavailable(ServiceUnavailableException ex) {
        return new ErrorResponse(
            "REGISTRATION_TEMPORARILY_UNAVAILABLE",
            "We are unable to complete your registration right now due to a temporary system issue. Please try again in a few minutes."
        );
    }
    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // Sends 503 Status Code
    public ErrorResponse handleRoleNotFound(RoleNotFoundException ex) {
        return new ErrorResponse(
            "NOT_AUTHORIZED",
            "You are not authorized to access this."
        );
    }
}
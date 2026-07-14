package com.example.authservice.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private long timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
}
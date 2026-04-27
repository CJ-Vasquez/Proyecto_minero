package com.minero.logistica.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    
    private final String code;
    
    public BadRequestException(String message) {
        super(message);
        this.code = "BAD_REQUEST_001";
    }
    
    public BadRequestException(String message, String code) {
        super(message);
        this.code = code;
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.code = "BAD_REQUEST_001";
    }
}
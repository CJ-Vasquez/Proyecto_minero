package com.minero.logistica.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ApiResponse<Map<String, String>> {
    
    private LocalDateTime timestamp;
    private String path;
    
    public ValidationErrorResponse(String message, Map<String, String> errors, String path) {
        super(false, message, errors, null);
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
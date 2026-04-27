package com.minero.logistica.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> validationErrors;
    
    public ValidationErrorDetails(ErrorDetails errorDetails, Map<String, String> validationErrors) {
        super(errorDetails.getTimestamp(), errorDetails.getStatus(), 
              errorDetails.getError(), errorDetails.getMessage(), 
              errorDetails.getPath(), errorDetails.getTraceId());
        this.validationErrors = validationErrors;
    }
}
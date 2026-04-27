package com.minero.logistica.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Manejo de ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("ResourceNotFoundException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Recurso no encontrado",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Manejo de BadRequestException (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("BadRequestException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Solicitud incorrecta",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de BusinessException (400) - Reglas de negocio
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("BusinessException [{}] - Code: {}: {}", traceId, ex.getCode(), ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de negocio - " + ex.getCode(),
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de UnauthorizedException (401)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("UnauthorizedException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "No autorizado",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Manejo de BadCredentialsException (401)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("BadCredentialsException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Credenciales inválidas",
            "Usuario o contraseña incorrectos",
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Manejo de UsernameNotFoundException (401)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("UsernameNotFoundException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Usuario no encontrado",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Manejo de AccessDeniedException (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("AccessDeniedException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Acceso denegado",
            "No tiene permisos suficientes para acceder a este recurso",
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Manejo de ForbiddenException (403)
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("ForbiddenException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Acceso denegado",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Manejo de MethodArgumentNotValidException (400) - Validación de @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("MethodArgumentNotValidException [{}]: {}", traceId, ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorDetails baseError = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación",
            "Datos inválidos en la solicitud",
            request.getRequestURI(),
            traceId
        );
        
        ValidationErrorDetails errorDetails = new ValidationErrorDetails(baseError, validationErrors);
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de ConstraintViolationException (400) - Validación de parámetros
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorDetails> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("ConstraintViolationException [{}]: {}", traceId, ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            validationErrors.put(propertyPath, message);
        }
        
        ErrorDetails baseError = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación",
            "Parámetros inválidos",
            request.getRequestURI(),
            traceId
        );
        
        ValidationErrorDetails errorDetails = new ValidationErrorDetails(baseError, validationErrors);
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de MethodArgumentTypeMismatchException (400)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("MethodArgumentTypeMismatchException [{}]: {}", traceId, ex.getMessage());
        
        String message = String.format("El parámetro '%s' debe ser de tipo '%s'", 
            ex.getName(), ex.getRequiredType().getSimpleName());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Tipo de parámetro inválido",
            message,
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de MissingServletRequestParameterException (400)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("MissingServletRequestParameterException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Parámetro faltante",
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de HttpMessageNotReadableException (400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("HttpMessageNotReadableException [{}]: {}", traceId, ex.getMessage());
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Formato de solicitud inválido",
            "El cuerpo de la solicitud tiene un formato inválido o falta algún campo requerido",
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Manejo de RuntimeException (500) - Error genérico
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("RuntimeException [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Manejo de Exception (500) - Error genérico final
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        String traceId = UUID.randomUUID().toString();
        log.error("Exception [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
            request.getRequestURI(),
            traceId
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
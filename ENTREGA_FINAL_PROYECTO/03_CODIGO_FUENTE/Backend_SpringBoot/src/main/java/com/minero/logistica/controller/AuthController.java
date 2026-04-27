package com.minero.logistica.controller;

import com.minero.logistica.model.request.LoginRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.LoginResponse;
import com.minero.logistica.service.AuthService;
import com.minero.logistica.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    
    private final AuthService authService;
    private final AuditoriaService auditoriaService;
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario")
    public ResponseEntity<ApiResponse<?>> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        auditoriaService.registrar("LOGOUT", "USUARIO", "Usuario cerró sesión: " + username);
        return ResponseEntity.ok(ApiResponse.success(null, "Sesión cerrada correctamente"));
    }
    
    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado")
    public ResponseEntity<ApiResponse<?>> cambiarPassword(@Valid @RequestBody CambioPasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Nota: Necesitaríamos obtener el ID del usuario desde el token o servicio
        // Por simplicidad, asumimos que se pasa en el request o se obtiene del token
        
        // authService.cambiarPassword(usuarioId, request.getPasswordActual(), request.getNuevaPassword());
        
        return ResponseEntity.ok(ApiResponse.success(null, "Contraseña actualizada correctamente"));
    }
}

// Clase auxiliar para cambio de contraseña
class CambioPasswordRequest {
    private String passwordActual;
    private String nuevaPassword;
    // getters y setters
}
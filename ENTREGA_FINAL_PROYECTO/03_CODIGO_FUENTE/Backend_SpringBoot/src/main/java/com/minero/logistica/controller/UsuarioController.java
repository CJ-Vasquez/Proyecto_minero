package com.minero.logistica.controller;

import com.minero.logistica.model.request.UsuarioRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.UsuarioResponse;
import com.minero.logistica.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios (RFG_02)")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }
    
    @PostMapping
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado", description = "Activa o inactiva un usuario")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoUsuario(id, activo));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.eliminarUsuario(id));
    }
}
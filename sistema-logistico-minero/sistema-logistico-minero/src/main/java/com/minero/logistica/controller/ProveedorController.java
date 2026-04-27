package com.minero.logistica.controller;

import com.minero.logistica.model.request.ProveedorRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.ProveedorResponse;
import com.minero.logistica.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Endpoints para gestión de proveedores (CUS09)")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {
    
    private final ProveedorService proveedorService;
    
    @GetMapping
    @Operation(summary = "Listar todos los proveedores")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProveedorResponse>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }
    
    @GetMapping("/activos")
    @Operation(summary = "Listar proveedores activos")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProveedorResponse>> listarActivos() {
        return ResponseEntity.ok(proveedorService.listarProveedoresActivos());
    }
    
    @GetMapping("/preferentes")
    @Operation(summary = "Listar proveedores preferentes", description = "Retorna proveedores con prioridad A o B")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProveedorResponse>> listarPreferentes() {
        return ResponseEntity.ok(proveedorService.listarProveedoresPreferentes());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerProveedor(id));
    }
    
    @PostMapping
    @Operation(summary = "Registrar proveedor", description = "Registra un nuevo proveedor en el sistema")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ProveedorResponse> registrar(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.registrarProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor", description = "Actualiza los datos de un proveedor")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ProveedorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, request));
    }
    
    @PostMapping("/{id}/evaluar")
    @Operation(summary = "Evaluar proveedor", description = "Evalúa a un proveedor y asigna prioridad (A, B, C)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ApiResponse<?>> evaluar(
            @PathVariable Long id,
            @RequestParam Double puntaje) {
        return ResponseEntity.ok(proveedorService.evaluarProveedor(id, puntaje));
    }
    
    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado", description = "Cambia el estado del proveedor (ACTIVO, INACTIVO, VETADO)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ApiResponse<?>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(proveedorService.cambiarEstadoProveedor(id, estado));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor del sistema")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.eliminarProveedor(id));
    }
}
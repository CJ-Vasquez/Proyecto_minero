package com.minero.logistica.controller;

import com.minero.logistica.model.request.RecepcionRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.RecepcionResponse;
import com.minero.logistica.service.RecepcionService;
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
@RequestMapping("/api/recepciones")
@RequiredArgsConstructor
@Tag(name = "Recepciones", description = "Endpoints para gestión de recepción de suministros (CUS12)")
@CrossOrigin(origins = "http://localhost:4200")
public class RecepcionController {
    
    private final RecepcionService recepcionService;
    
    @GetMapping
    @Operation(summary = "Listar todas las recepciones")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<RecepcionResponse>> listarTodas() {
        return ResponseEntity.ok(recepcionService.listarRecepciones());
    }
    
    @GetMapping("/pendientes")
    @Operation(summary = "Listar recepciones pendientes")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<RecepcionResponse>> listarPendientes() {
        return ResponseEntity.ok(recepcionService.listarRecepcionesPendientes());
    }
    
    @GetMapping("/almacen/{almacen}")
    @Operation(summary = "Listar por almacén")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<RecepcionResponse>> listarPorAlmacen(@PathVariable String almacen) {
        return ResponseEntity.ok(recepcionService.listarPorAlmacen(almacen));
    }
    
    @GetMapping("/orden-compra/{ordenCompraId}")
    @Operation(summary = "Listar por orden de compra")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<RecepcionResponse>> listarPorOrdenCompra(@PathVariable Long ordenCompraId) {
        return ResponseEntity.ok(recepcionService.listarPorOrdenCompra(ordenCompraId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener recepción por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<RecepcionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionService.obtenerRecepcion(id));
    }
    
    @PostMapping
    @Operation(summary = "Registrar recepción", description = "Registra la recepción de suministros (CUS12, RF_20, RF_02)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<RecepcionResponse> registrar(@Valid @RequestBody RecepcionRequest request) {
        RecepcionResponse response = recepcionService.registrarRecepcion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}/completar")
    @Operation(summary = "Completar recepción", description = "Marca la recepción como completada")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<RecepcionResponse> completar(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionService.completarRecepcion(id));
    }
    
    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular recepción")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<RecepcionResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionService.anularRecepcion(id));
    }
}
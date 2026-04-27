package com.minero.logistica.controller;

import com.minero.logistica.model.request.OrdenSalidaRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.OrdenSalidaResponse;
import com.minero.logistica.service.OrdenSalidaService;
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
@RequestMapping("/api/ordenes-salida")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Salida", description = "Endpoints para gestión de órdenes de salida (CUS15, CUS16)")
@CrossOrigin(origins = "http://localhost:4200")
public class OrdenSalidaController {
    
    private final OrdenSalidaService ordenSalidaService;
    
    @GetMapping
    @Operation(summary = "Listar todas las órdenes de salida")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<OrdenSalidaResponse>> listarTodas() {
        return ResponseEntity.ok(ordenSalidaService.listarOrdenesSalida());
    }
    
    @GetMapping("/pendientes")
    @Operation(summary = "Listar órdenes pendientes de aprobación", description = "Retorna órdenes de salida pendientes de aprobación (CUS16)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<List<OrdenSalidaResponse>> listarPendientesAprobacion() {
        return ResponseEntity.ok(ordenSalidaService.listarPendientesAprobacion());
    }
    
    @GetMapping("/almacen/{almacen}")
    @Operation(summary = "Listar por almacén de origen")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<OrdenSalidaResponse>> listarPorAlmacen(@PathVariable String almacen) {
        return ResponseEntity.ok(ordenSalidaService.listarPorAlmacenOrigen(almacen));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden de salida por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<OrdenSalidaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenSalidaService.obtenerOrdenSalida(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear orden de salida", description = "Crea una nueva orden de salida (CUS15, RF_19, RF_24)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<OrdenSalidaResponse> crear(@Valid @RequestBody OrdenSalidaRequest request) {
        OrdenSalidaResponse response = ordenSalidaService.crearOrdenSalida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar orden de salida", description = "Aprueba una orden de salida (CUS16, RF_18)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<OrdenSalidaResponse> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(ordenSalidaService.aprobarOrdenSalida(id));
    }
    
    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular orden de salida")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<OrdenSalidaResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ordenSalidaService.anularOrdenSalida(id));
    }
}
package com.minero.logistica.controller;

import com.minero.logistica.model.request.CotizacionRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.CotizacionResponse;
import com.minero.logistica.service.CotizacionService;
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
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones", description = "Endpoints para gestión de cotizaciones (CUS05)")
@CrossOrigin(origins = "http://localhost:4200")
public class CotizacionController {
    
    private final CotizacionService cotizacionService;
    
    @GetMapping
    @Operation(summary = "Listar todas las cotizaciones")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<CotizacionResponse>> listarTodas() {
        return ResponseEntity.ok(cotizacionService.listarCotizaciones());
    }
    
    @GetMapping("/pendientes")
    @Operation(summary = "Listar cotizaciones pendientes", description = "Retorna cotizaciones pendientes de aprobación (CUS05)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CotizacionResponse>> listarPendientes() {
        return ResponseEntity.ok(cotizacionService.listarPendientesAprobacion());
    }
    
    @GetMapping("/vigentes")
    @Operation(summary = "Listar cotizaciones vigentes", description = "Retorna cotizaciones con fecha de validez vigente")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<CotizacionResponse>> listarVigentes() {
        return ResponseEntity.ok(cotizacionService.listarVigentes());
    }
    
    @GetMapping("/solicitud/{solicitudId}")
    @Operation(summary = "Listar por solicitud", description = "Retorna cotizaciones de una solicitud específica")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<CotizacionResponse>> listarPorSolicitud(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(cotizacionService.listarPorSolicitudPedido(solicitudId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cotización por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<CotizacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.obtenerCotizacion(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear cotización", description = "Crea una nueva cotización (CUS05, RF_10, RF_12, RF_13)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<CotizacionResponse> crear(@Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse response = cotizacionService.crearCotizacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{id}/enviar-proveedor")
    @Operation(summary = "Enviar a proveedor", description = "Envía la cotización al proveedor")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ApiResponse<?>> enviarAProveedor(@PathVariable Long id) {
        cotizacionService.enviarAProveedor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cotización enviada al proveedor"));
    }
    
    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar cotización", description = "Aprueba una cotización (CUS03, RF_06)")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CotizacionResponse> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.aprobarCotizacion(id));
    }
    
    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar cotización", description = "Rechaza una cotización (CUS03, RF_07)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CotizacionResponse> rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(cotizacionService.rechazarCotizacion(id, motivo));
    }
    
    @PutMapping("/{id}/recibir-respuesta")
    @Operation(summary = "Recibir respuesta del proveedor", description = "Registra la respuesta del proveedor a la cotización")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<CotizacionResponse> recibirRespuesta(
            @PathVariable Long id,
            @RequestParam Double precio,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(cotizacionService.recibirRespuestaProveedor(id, precio, observaciones));
    }
}
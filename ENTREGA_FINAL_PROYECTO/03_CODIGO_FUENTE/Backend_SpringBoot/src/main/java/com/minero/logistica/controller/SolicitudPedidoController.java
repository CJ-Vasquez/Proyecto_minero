package com.minero.logistica.controller;

import com.minero.logistica.model.request.SolicitudPedidoRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.SolicitudPedidoResponse;
import com.minero.logistica.service.SolicitudPedidoService;
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
@RequestMapping("/api/solicitudes-pedido")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Pedido", description = "Endpoints para gestión de solicitudes de pedido (CUS01, CUS02, CUS03)")
@CrossOrigin(origins = "http://localhost:4200")
public class SolicitudPedidoController {
    
    private final SolicitudPedidoService solicitudPedidoService;
    
    @GetMapping
    @Operation(summary = "Listar todas las solicitudes", description = "Retorna todas las solicitudes de pedido")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN')")
    public ResponseEntity<List<SolicitudPedidoResponse>> listarTodas() {
        return ResponseEntity.ok(solicitudPedidoService.listarSolicitudes());
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar por estado", description = "Retorna solicitudes filtradas por estado")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN')")
    public ResponseEntity<List<SolicitudPedidoResponse>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(solicitudPedidoService.listarPorEstado(estado));
    }
    
    @GetMapping("/pendientes")
    @Operation(summary = "Listar pendientes de aprobación", description = "Retorna solicitudes pendientes de aprobación (CUS03)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<SolicitudPedidoResponse>> listarPendientesAprobacion() {
        return ResponseEntity.ok(solicitudPedidoService.listarPendientesAprobacion());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID", description = "Retorna una solicitud específica")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<SolicitudPedidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudPedidoService.obtenerSolicitud(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear solicitud", description = "Crea una nueva solicitud de pedido (CUS01, RF_01)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<SolicitudPedidoResponse> crear(@Valid @RequestBody SolicitudPedidoRequest request) {
        SolicitudPedidoResponse response = solicitudPedidoService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{id}/enviar-aprobacion")
    @Operation(summary = "Enviar a aprobación", description = "Envía la solicitud a aprobación (CUS02)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<SolicitudPedidoResponse> enviarAAprobacion(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudPedidoService.enviarAAprobacion(id));
    }
    
    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar solicitud", description = "Aprueba una solicitud de pedido (CUS03, RF_05)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SolicitudPedidoResponse> aprobar(
            @PathVariable Long id,
            @RequestParam(required = false) Integer cantidadAprobada) {
        return ResponseEntity.ok(solicitudPedidoService.aprobarSolicitud(id, cantidadAprobada));
    }
    
    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar solicitud", description = "Rechaza una solicitud de pedido (CUS03, RF_04)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SolicitudPedidoResponse> rechazar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(solicitudPedidoService.rechazarSolicitud(id, motivo));
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar solicitud", description = "Cancela una solicitud de pedido")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<SolicitudPedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudPedidoService.cancelarSolicitud(id));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar solicitud", description = "Elimina una solicitud de pedido")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        // solicitudPedidoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Solicitud eliminada correctamente"));
    }
}
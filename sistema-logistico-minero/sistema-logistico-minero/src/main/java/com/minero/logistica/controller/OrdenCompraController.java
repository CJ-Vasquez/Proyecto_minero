package com.minero.logistica.controller;

import com.minero.logistica.model.request.OrdenCompraRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.OrdenCompraResponse;
import com.minero.logistica.service.OrdenCompraService;
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
@RequestMapping("/api/ordenes-compra")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Compra", description = "Endpoints para gestión de órdenes de compra (CUS06)")
@CrossOrigin(origins = "http://localhost:4200")
public class OrdenCompraController {
    
    private final OrdenCompraService ordenCompraService;
    
    @GetMapping
    @Operation(summary = "Listar todas las órdenes de compra")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<OrdenCompraResponse>> listarTodas() {
        return ResponseEntity.ok(ordenCompraService.listarOrdenesCompra());
    }
    
    @GetMapping("/pendientes")
    @Operation(summary = "Listar órdenes pendientes de envío")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<OrdenCompraResponse>> listarPendientesEnvio() {
        return ResponseEntity.ok(ordenCompraService.listarPendientesEnvio());
    }
    
    @GetMapping("/proveedor/{proveedorId}")
    @Operation(summary = "Listar por proveedor", description = "Retorna órdenes de compra de un proveedor específico")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<OrdenCompraResponse>> listarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(ordenCompraService.listarPorProveedor(proveedorId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden de compra por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<OrdenCompraResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.obtenerOrdenCompra(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear orden de compra", description = "Crea una nueva orden de compra (CUS06, RF_14)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<OrdenCompraResponse> crear(@Valid @RequestBody OrdenCompraRequest request) {
        OrdenCompraResponse response = ordenCompraService.crearOrdenCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/desde-cotizacion/{cotizacionId}")
    @Operation(summary = "Crear desde cotización", description = "Crea una orden de compra a partir de una cotización aprobada")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<OrdenCompraResponse> crearDesdeCotizacion(@PathVariable Long cotizacionId) {
        return ResponseEntity.ok(ordenCompraService.crearOrdenCompraDesdeCotizacion(cotizacionId));
    }
    
    @PutMapping("/{id}/enviar")
    @Operation(summary = "Enviar orden de compra", description = "Envía la orden de compra al proveedor")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<OrdenCompraResponse> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.enviarOrdenCompra(id));
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar orden de compra", description = "Cancela una orden de compra")
    //@PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<OrdenCompraResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.cancelarOrdenCompra(id));
    }
}
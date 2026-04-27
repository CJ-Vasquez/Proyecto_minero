package com.minero.logistica.controller;

import com.minero.logistica.model.request.ProductoRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.ProductoResponse;
import com.minero.logistica.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para gestión de productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @GetMapping
    @Operation(summary = "Listar todos los productos")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }
    
    @GetMapping("/activos")
    @Operation(summary = "Listar productos activos")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProductoResponse>> listarActivos() {
        return ResponseEntity.ok(productoService.listarProductosActivos());
    }
    
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar por categoría")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<ProductoResponse>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.listarProductosPorCategoria(categoria));
    }
    
    @GetMapping("/stock-critico")
    @Operation(summary = "Listar productos con stock crítico", description = "Retorna productos con stock actual <= stock mínimo (RF_14)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<ProductoResponse>> listarStockCritico() {
        return ResponseEntity.ok(productoService.listarProductosStockCritico());
    }
    
    @GetMapping("/sin-stock")
    @Operation(summary = "Listar productos sin stock")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<List<ProductoResponse>> listarSinStock() {
        return ResponseEntity.ok(productoService.listarProductosSinStock());
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", description = "Búsqueda por nombre o descripción (RNF_04 - Autocompletar)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<List<ProductoResponse>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(productoService.buscarProductos(termino));
    }
    
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener por código", description = "Retorna un producto por su código")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ProductoResponse> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.obtenerProductoPorCodigo(codigo));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN', 'ASISTENTE_COMPRAS')")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }
    
    @PostMapping
    @Operation(summary = "Registrar producto", description = "Registra un nuevo producto")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<ProductoResponse> registrar(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, request));
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock de un producto (RF_14)")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN', 'ASISTENTE_ALMACEN')")
    public ResponseEntity<ApiResponse<?>> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(productoService.actualizarStock(id, cantidad));
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado", description = "Activa o inactiva un producto")
    //@PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    public ResponseEntity<ApiResponse<?>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        return ResponseEntity.ok(productoService.cambiarEstadoProducto(id, activo));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.eliminarProducto(id));
    }
}
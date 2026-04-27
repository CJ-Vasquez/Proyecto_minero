package com.minero.logistica.controller;

import com.minero.logistica.entity.KardexEntity;
import com.minero.logistica.service.KardexService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kardex")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class KardexController {

    private final KardexService kardexService;

    /**
     * Obtener historial completo de un producto
     * GET /api/kardex/producto/{productoId}
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<KardexEntity>> getHistorialByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(kardexService.getHistorialByProducto(productoId));
    }

    /**
     * Obtener stock actual de un producto
     * GET /api/kardex/producto/{productoId}/stock-actual
     */
    @GetMapping("/producto/{productoId}/stock-actual")
    public ResponseEntity<Integer> getStockActual(@PathVariable Long productoId) {
        return ResponseEntity.ok(kardexService.getStockActual(productoId));
    }

    /**
     * Obtener resumen de movimientos de un producto
     * GET /api/kardex/producto/{productoId}/resumen
     */
    @GetMapping("/producto/{productoId}/resumen")
    public ResponseEntity<Map<String, Object>> getResumenMovimientos(@PathVariable Long productoId) {
        return ResponseEntity.ok(kardexService.getResumenMovimientos(productoId));
    }

    /**
     * Obtener últimos 10 movimientos (para dashboard)
     * GET /api/kardex/ultimos
     */
    @GetMapping("/ultimos")
    public ResponseEntity<List<KardexEntity>> getUltimosMovimientos() {
        return ResponseEntity.ok(kardexService.getUltimosMovimientos());
    }

    /**
     * Obtener últimos N movimientos
     * GET /api/kardex/ultimos?limite=20
     */
    @GetMapping("/ultimos/limite")
    public ResponseEntity<List<KardexEntity>> getUltimosMovimientosConLimite(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(kardexService.getUltimosMovimientos(limite));
    }

    /**
     * Obtener movimientos mensuales para gráficos
     * GET /api/kardex/mensuales
     */
    @GetMapping("/mensuales")
    public ResponseEntity<Map<String, Object>> getMovimientosMensuales() {
        return ResponseEntity.ok(kardexService.getMovimientosMensuales());
    }

    /**
     * Obtener movimientos por rango de fechas
     * GET /api/kardex/fechas?inicio=2025-01-01T00:00:00&fin=2025-12-31T23:59:59
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<KardexEntity>> getMovimientosPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(kardexService.getMovimientosPorFechas(inicio, fin));
    }

    /**
     * Obtener movimientos por almacén
     * GET /api/kardex/almacen/{almacen}
     */
    @GetMapping("/almacen/{almacen}")
    public ResponseEntity<List<KardexEntity>> getMovimientosPorAlmacen(@PathVariable String almacen) {
        return ResponseEntity.ok(kardexService.getMovimientosPorAlmacen(almacen));
    }

    /**
     * Obtener movimientos por tipo (ENTRADA/SALIDA) de un producto
     * GET /api/kardex/producto/{productoId}/tipo/{tipo}
     */
    @GetMapping("/producto/{productoId}/tipo/{tipo}")
    public ResponseEntity<List<KardexEntity>> getMovimientosPorTipo(
            @PathVariable Long productoId,
            @PathVariable String tipo) {
        return ResponseEntity.ok(kardexService.getMovimientosPorTipo(productoId, tipo));
    }

    /**
     * Obtener movimientos por documento
     * GET /api/kardex/documento/{documento}/{numeroDocumento}
     */
    @GetMapping("/documento/{documento}/{numeroDocumento}")
    public ResponseEntity<List<KardexEntity>> getMovimientosPorDocumento(
            @PathVariable String documento,
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(kardexService.getMovimientosPorDocumento(documento, numeroDocumento));
    }
    
    @GetMapping
    public ResponseEntity<List<KardexEntity>> getTodos() {
        return ResponseEntity.ok(kardexService.getTodos());
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<Map<String, Object>>> getResumen() {
        return ResponseEntity.ok(kardexService.getResumenProductos());
    }
}
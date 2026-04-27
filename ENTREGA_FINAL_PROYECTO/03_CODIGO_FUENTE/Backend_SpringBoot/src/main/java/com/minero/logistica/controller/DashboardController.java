package com.minero.logistica.controller;

import com.minero.logistica.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        return ResponseEntity.ok(dashboardService.getResumenDashboard());
    }

    @GetMapping("/stock-critico")
    public ResponseEntity<Map<String, Object>> getStockCritico() {
        return ResponseEntity.ok(dashboardService.getStockCritico());
    }

    @GetMapping("/movimientos-mensuales")
    public ResponseEntity<Map<String, Object>> getMovimientosMensuales() {
        return ResponseEntity.ok(dashboardService.getMovimientosMensuales());
    }
}
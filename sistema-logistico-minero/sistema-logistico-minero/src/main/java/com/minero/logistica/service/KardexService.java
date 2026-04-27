package com.minero.logistica.service;

import com.minero.logistica.entity.KardexEntity;
import com.minero.logistica.repository.KardexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KardexService {

    private final KardexRepository kardexRepository;

    /**
     * Registrar un movimiento en el kardex
     */
    @Transactional
    public KardexEntity registrarMovimiento(KardexEntity kardex) {
        log.info("Registrando movimiento en kardex - Producto: {}, Tipo: {}, Cantidad: {}", 
            kardex.getProducto().getId(), kardex.getTipo(), kardex.getCantidad());
        return kardexRepository.save(kardex);
    }

    /**
     * Obtener historial completo de un producto
     */
    public List<KardexEntity> getHistorialByProducto(Long productoId) {
        return kardexRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId);
    }

    /**
     * Obtener stock actual de un producto
     */
    public Integer getStockActual(Long productoId) {
        Integer stock = kardexRepository.findStockActualByProducto(productoId);
        return stock != null ? stock : 0;
    }

    /**
     * Obtener resumen de movimientos de un producto
     */
    public Map<String, Object> getResumenMovimientos(Long productoId) {
        Map<String, Object> resumen = new HashMap<>();
        Object[] resultado = kardexRepository.findResumenMovimientosByProducto(productoId);
        
        if (resultado != null && resultado.length >= 3) {
            resumen.put("productoId", resultado[0]);
            resumen.put("totalEntradas", resultado[1]);
            resumen.put("totalSalidas", resultado[2]);
            resumen.put("stockActual", getStockActual(productoId));
        } else {
            resumen.put("productoId", productoId);
            resumen.put("totalEntradas", 0);
            resumen.put("totalSalidas", 0);
            resumen.put("stockActual", 0);
        }
        
        return resumen;
    }

    /**
     * Obtener últimos movimientos (para dashboard)
     * @param limite Número de movimientos a obtener
     */
    public List<KardexEntity> getUltimosMovimientos(int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return kardexRepository.findAllByOrderByFechaMovimientoDesc(pageable);
    }

    /**
     * Obtener últimos 10 movimientos (para dashboard)
     */
    public List<KardexEntity> getUltimosMovimientos() {
        return getUltimosMovimientos(10);
    }

    /**
     * Obtener movimientos por rango de fechas
     */
    public List<KardexEntity> getMovimientosPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return kardexRepository.findByFechaMovimientoBetween(inicio, fin);
    }

    /**
     * Obtener movimientos por almacén
     */
    public List<KardexEntity> getMovimientosPorAlmacen(String almacen) {
        return kardexRepository.findByAlmacen(almacen);
    }

    /**
     * Obtener movimientos por tipo (ENTRADA/SALIDA) de un producto
     */
    public List<KardexEntity> getMovimientosPorTipo(Long productoId, String tipo) {
        return kardexRepository.findByProductoIdAndTipo(productoId, tipo);
    }

    /**
     * Obtener movimientos mensuales para gráficos del dashboard
     */
    public Map<String, Object> getMovimientosMensuales() {
        Map<String, Object> response = new HashMap<>();
        
        // Inicializar arrays para 12 meses
        int[] entradas = new int[12];
        int[] salidas = new int[12];
        double totalCompras = 0;
        double totalSalidas = 0;
        
        // Obtener todos los movimientos del año actual
        LocalDateTime inicioAnio = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finAnio = LocalDateTime.now().withDayOfYear(LocalDateTime.now().getDayOfYear()).withHour(23).withMinute(59).withSecond(59);
        
        List<KardexEntity> movimientos = kardexRepository.findByFechaMovimientoBetween(inicioAnio, finAnio);
        
        for (KardexEntity k : movimientos) {
            int mes = k.getFechaMovimiento().getMonthValue() - 1;
            if ("ENTRADA".equals(k.getTipo())) {
                entradas[mes] += k.getCantidad();
                totalCompras += k.getSubtotal() != null ? k.getSubtotal() : 0;
            } else if ("SALIDA".equals(k.getTipo())) {
                salidas[mes] += k.getCantidad();
                totalSalidas += k.getSubtotal() != null ? k.getSubtotal() : 0;
            }
        }
        
        response.put("entradas", entradas);
        response.put("salidas", salidas);
        response.put("totalCompras", totalCompras);
        response.put("totalSalidas", totalSalidas);
        
        return response;
    }

    /**
     * Obtener movimientos por documento
     */
    public List<KardexEntity> getMovimientosPorDocumento(String documento, String numeroDocumento) {
        return kardexRepository.findByDocumentoAndNumeroDocumento(documento, numeroDocumento);
    }

    /**
     * Obtener historial de un producto en un rango de fechas
     */
    public List<KardexEntity> getHistorialByProductoAndFechas(Long productoId, LocalDateTime inicio, LocalDateTime fin) {
        return kardexRepository.findHistorialByProductoAndFechas(productoId, inicio, fin);
    }
    
    /**
     * Obtener todos los movimientos
     */
    public List<KardexEntity> getTodos() {
        return kardexRepository.findAllByOrderByFechaMovimientoDesc(PageRequest.of(0, 500));
    }

    /**
     * Obtener resumen por producto (para tabla de stock)
     */
    public List<Map<String, Object>> getResumenProductos() {
        List<KardexEntity> todos = getTodos();
        Map<Long, Map<String, Object>> resumenMap = new HashMap<>();

        for (KardexEntity k : todos) {
            Long productoId = k.getProducto().getId();
            Map<String, Object> r = resumenMap.computeIfAbsent(productoId, id -> {
                Map<String, Object> nuevo = new HashMap<>();
                Integer stock = k.getProducto().getStockActual() != null ? k.getProducto().getStockActual() : 0;
                Double precio = k.getProducto().getPrecioReferencial() != null ? k.getProducto().getPrecioReferencial() : 0.0;
                nuevo.put("productoId", id);
                nuevo.put("codigo", k.getProducto().getCodigo());
                nuevo.put("nombre", k.getProducto().getNombre());
                nuevo.put("entradas", 0);
                nuevo.put("salidas", 0);
                nuevo.put("saldoActual", stock);
                nuevo.put("valorizado", stock * precio);
                return nuevo;
            });

            int cantidad = k.getCantidad() != null ? k.getCantidad() : 0;
            if ("ENTRADA".equalsIgnoreCase(k.getTipo())) {
                r.put("entradas", ((Integer) r.get("entradas")) + cantidad);
            } else if ("SALIDA".equalsIgnoreCase(k.getTipo())) {
                r.put("salidas", ((Integer) r.get("salidas")) + cantidad);
            }
        }

        return new java.util.ArrayList<>(resumenMap.values());
    }
}
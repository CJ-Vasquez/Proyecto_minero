package com.minero.logistica.service;

import com.minero.logistica.entity.KardexEntity;
import com.minero.logistica.entity.ProductoEntity;
import com.minero.logistica.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final ProductoRepository productoRepository;
    private final SolicitudPedidoRepository solicitudPedidoRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final RecepcionRepository recepcionRepository;
    private final OrdenSalidaRepository ordenSalidaRepository;
    private final KardexRepository kardexRepository;
    
    /**
     * Obtener resumen del dashboard con todos los indicadores
     */
    public Map<String, Object> getResumenDashboard() {
        Map<String, Object> response = new HashMap<>();
        
        // ==================== PRODUCTOS ====================
        response.put("totalProductosActivos", productoRepository.findByActivoTrue().size());
        response.put("productosStockCritico", productoRepository.findProductosConStockCritico().size());
        response.put("productosSinStock", productoRepository.findProductosSinStock().size());
        
        // ==================== SOLICITUDES ====================
        response.put("solicitudesPendientes", solicitudPedidoRepository.findByEstado("PENDIENTE_APROBACION").size());
        response.put("solicitudesAprobadasMes", getSolicitudesAprobadasMes());
        response.put("solicitudesRechazadasMes", getSolicitudesRechazadasMes());
        
        // ==================== ÓRDENES DE COMPRA ====================
        response.put("ordenesCompraPendientes", ordenCompraRepository.findOrdenesPendientesEnvio().size());
        response.put("ordenesCompraEnviadasMes", getOrdenesCompraEnviadasMes());
        response.put("totalComprasMes", obtenerTotalComprasMes());
        
        // ==================== ÓRDENES DE SALIDA ====================
        response.put("ordenesSalidaPendientes", ordenSalidaRepository.findOrdenesPendientesAprobacion().size());
        response.put("ordenesSalidaAprobadasMes", getOrdenesSalidaAprobadasMes());
        response.put("totalSalidasMes", obtenerTotalSalidasMes());
        
        // ==================== RECEPCIONES ====================
        response.put("recepcionesPendientes", recepcionRepository.findRecepcionesPendientes().size());
        response.put("recepcionesCompletadasMes", getRecepcionesCompletadasMes());
        
        // ==================== COTIZACIONES ====================
        response.put("cotizacionesPendientes", getCotizacionesPendientes());
        response.put("cotizacionesAprobadasMes", getCotizacionesAprobadasMes());
        
        // ==================== KARDEX - ÚLTIMOS MOVIMIENTOS ====================
        Pageable pageable = PageRequest.of(0, 10);
        List<KardexEntity> ultimosMovimientos = kardexRepository.findAllByOrderByFechaMovimientoDesc(pageable);
        response.put("ultimosMovimientos", convertKardexToResponse(ultimosMovimientos));
        
        // ==================== MOVIMIENTOS MENSUALES ====================
        response.put("movimientosMensuales", getMovimientosMensuales());
        
        // ==================== PRODUCTOS POR CATEGORÍA ====================
        response.put("productosPorCategoria", getProductosPorCategoria());
        
        return response;
    }
    
    /**
     * Obtener productos con stock crítico detallado
     */
    public Map<String, Object> getStockCritico() {
        Map<String, Object> response = new HashMap<>();
        
        List<ProductoEntity> productosCriticos = productoRepository.findProductosConStockCritico();
        
        response.put("productosCriticos", productosCriticos);
        response.put("totalProductosCriticos", productosCriticos.size());
        
        // Contar por categoría
        Map<String, Integer> productosPorCategoria = new HashMap<>();
        for (ProductoEntity producto : productosCriticos) {
            productosPorCategoria.merge(producto.getCategoria(), 1, Integer::sum);
        }
        response.put("productosPorCategoria", productosPorCategoria);
        
        return response;
    }
    
    /**
     * Obtener movimientos mensuales para gráficos
     */
    public Map<String, Object> getMovimientosMensuales() {
        Map<String, Object> response = new HashMap<>();
        
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime inicioMes = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime finMes = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        
        List<KardexEntity> movimientosMes = kardexRepository.findByFechaMovimientoBetween(inicioMes, finMes);
        
        int entradas = 0;
        int salidas = 0;
        double valorEntradas = 0;
        double valorSalidas = 0;
        
        for (KardexEntity k : movimientosMes) {
            if ("ENTRADA".equals(k.getTipo())) {
                entradas += k.getCantidad();
                valorEntradas += k.getSubtotal() != null ? k.getSubtotal() : 0;
            } else if ("SALIDA".equals(k.getTipo())) {
                salidas += k.getCantidad();
                valorSalidas += k.getSubtotal() != null ? k.getSubtotal() : 0;
            }
        }
        
        response.put("mes", currentMonth.getMonth().toString());
        response.put("anio", currentMonth.getYear());
        response.put("totalEntradas", entradas);
        response.put("totalSalidas", salidas);
        response.put("valorEntradas", valorEntradas);
        response.put("valorSalidas", valorSalidas);
        response.put("totalMovimientos", entradas + salidas);
        
        return response;
    }
    
    /**
     * Obtener productos agrupados por categoría
     */
    private Map<String, Integer> getProductosPorCategoria() {
        List<Object[]> resultados = productoRepository.countProductosByCategoria();
        Map<String, Integer> categorias = new HashMap<>();
        for (Object[] row : resultados) {
            categorias.put((String) row[0], ((Number) row[1]).intValue());
        }
        return categorias;
    }
    
    private Integer getSolicitudesAprobadasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return solicitudPedidoRepository.findByEstadoAndFechaBetween("APROBADO", inicioMes, finMes).size();
    }
    
    private Integer getSolicitudesRechazadasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return solicitudPedidoRepository.findByEstadoAndFechaBetween("RECHAZADO", inicioMes, finMes).size();
    }
    
    private Integer getOrdenesCompraEnviadasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return (int) ordenCompraRepository.findByFechaBetween(inicioMes, finMes).stream()
                .filter(o -> "ENVIADO".equals(o.getEstado()))
                .count();
    }
    
    private Integer getOrdenesSalidaAprobadasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return (int) ordenSalidaRepository.findByFechaBetween(inicioMes, finMes).stream()
                .filter(o -> "APROBADO".equals(o.getEstado()))
                .count();
    }
    
    private Integer getRecepcionesCompletadasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fin = finMes.atTime(23, 59, 59);
        return (int) recepcionRepository.findByFechaRecepcionBetween(inicio, fin).stream()
                .filter(r -> "COMPLETADO".equals(r.getEstado()))
                .count();
    }
    
    private Integer getCotizacionesPendientes() {
        // Este método debe ser implementado según tu repositorio de cotizaciones
        // Por ahora retorna 0
        return 0;
    }
    
    private Integer getCotizacionesAprobadasMes() {
        // Este método debe ser implementado según tu repositorio de cotizaciones
        // Por ahora retorna 0
        return 0;
    }
    
    private Double obtenerTotalComprasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return ordenCompraRepository.findByFechaBetween(inicioMes, finMes).stream()
                .filter(o -> "ENVIADO".equals(o.getEstado()))
                .mapToDouble(o -> o.getMontoTotal() != null ? o.getMontoTotal() : 0)
                .sum();
    }
    
    private Double obtenerTotalSalidasMes() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate inicioMes = currentMonth.atDay(1);
        LocalDate finMes = currentMonth.atEndOfMonth();
        return ordenSalidaRepository.findByFechaBetween(inicioMes, finMes).stream()
                .filter(o -> "APROBADO".equals(o.getEstado()))
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0)
                .sum();
    }
    
    private List<Map<String, Object>> convertKardexToResponse(List<KardexEntity> entities) {
        return entities.stream().map(k -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", k.getId());
            map.put("productoId", k.getProducto().getId());
            map.put("codigoProducto", k.getProducto().getCodigo());
            map.put("nombreProducto", k.getProducto().getNombre());
            map.put("tipo", k.getTipo());
            map.put("documento", k.getDocumento());
            map.put("numeroDocumento", k.getNumeroDocumento());
            map.put("cantidad", k.getCantidad());
            map.put("precioUnitario", k.getPrecioUnitario());
            map.put("subtotal", k.getSubtotal());
            map.put("stockAnterior", k.getStockAnterior());
            map.put("stockNuevo", k.getStockNuevo());
            map.put("almacen", k.getAlmacen());
            map.put("usuario", k.getUsuario());
            map.put("fechaMovimiento", k.getFechaMovimiento());
            return map;
        }).collect(Collectors.toList());
    }
}
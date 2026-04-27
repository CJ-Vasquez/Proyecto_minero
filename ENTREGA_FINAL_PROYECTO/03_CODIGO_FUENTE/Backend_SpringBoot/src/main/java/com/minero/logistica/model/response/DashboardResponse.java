package com.minero.logistica.model.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    
    private Integer totalProductosActivos;
    private Integer productosStockCritico;
    private Integer productosSinStock;
    private Integer solicitudesPendientes;
    private Integer solicitudesAprobadasMes;
    private Integer ordenesCompraPendientes;
    private Integer ordenesSalidaPendientes;
    private Integer recepcionesPendientes;
    private Double totalComprasMes;
    private Double totalSalidasMes;
    private List<KardexResponse> ultimosMovimientos;
    
    @Data
    public static class StockCriticoDTO {
        private List<ProductoResponse> productosCriticos;
        private Map<String, Integer> productosPorCategoria;
        private Integer totalProductosCriticos;
    }
    
    @Data
    public static class MovimientosMensualesDTO {
        private String mes;
        private Integer anio;
        private Double totalCompras;
        private Double totalSalidas;
    }
}
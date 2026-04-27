package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReporteResponse {
    
    // Reporte de compras
    @Data
    public static class ReporteComprasDTO {
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Integer totalOrdenes;
        private Double montoTotal;
        private List<ResumenProveedorDTO> resumenPorProveedor;
        private List<OrdenCompraResponse> ordenes;
    }
    
    // Reporte de almacén
    @Data
    public static class ReporteAlmacenDTO {
        private String almacen;
        private LocalDateTime fechaGeneracion;
        private Integer totalProductos;
        private Integer stockValorizado;
        private List<ResumenProductoStockDTO> productos;
    }
    
    // Reporte de kardex
    @Data
    public static class ReporteKardexDTO {
        private Long productoId;
        private String nombreProducto;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private Integer stockInicial;
        private Integer stockFinal;
        private List<KardexResponse> movimientos;
        private ResumenMovimientosDTO resumen;
    }
    
    // Reporte de proveedores
    @Data
    public static class ReporteProveedoresDTO {
        private LocalDateTime fechaGeneracion;
        private Integer totalProveedores;
        private Integer proveedoresActivos;
        private Integer proveedoresPreferentes;
        private List<ProveedorResponse> mejoresProveedores;
        private List<ResumenComprasProveedorDTO> comprasPorProveedor;
    }
    
    // Clases auxiliares
    @Data
    public static class ResumenProveedorDTO {
        private Long proveedorId;
        private String nombreProveedor;
        private Integer cantidadOrdenes;
        private Double montoTotal;
        private Double promedioCompra;
    }
    
    @Data
    public static class ResumenProductoStockDTO {
        private Long productoId;
        private String codigo;
        private String nombre;
        private String categoria;
        private Integer stockActual;
        private Integer stockMinimo;
        private Double precioReferencial;
        private Double valorizado;
        private String estado;  // CRITICO, NORMAL, EXCESO
    }
    
    @Data
    public static class ResumenMovimientosDTO {
        private Integer totalEntradas;
        private Integer totalSalidas;
        private Integer totalMovimientos;
        private Double montoEntradas;
        private Double montoSalidas;
    }
    
    @Data
    public static class ResumenComprasProveedorDTO {
        private Long proveedorId;
        private String nombreProveedor;
        private Integer cantidadOrdenes;
        private Double montoTotal;
        private Double porcentajeParticipacion;
    }
    
    @Data
    public static class ParametrosReporteDTO {
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String almacen;
        private Long productoId;
        private Long proveedorId;
        private String categoria;
        private String formato;  // PDF, XLS, DOC
    }
}
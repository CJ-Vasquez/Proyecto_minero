package com.minero.logistica.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ReporteRequest {
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
    
    private String almacen;
    private Long productoId;
    private Long proveedorId;
    private String categoria;
    private String tipo;  // COMPRAS, ALMACEN, KARDEX, PROVEEDORES
    private String formato;  // PDF, XLS, DOC
}
package com.minero.logistica.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class BusquedaRequest {
    
    private String termino;
    private String tipo;  // PRODUCTO, PROVEEDOR, SOLICITUD, ORDEN
    
    // Filtros de fechas
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
    
    // Filtros de estado
    private String estado;
    
    // Filtros de categoría
    private String categoria;
    
    // Paginación
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private String sortDirection = "DESC";
}
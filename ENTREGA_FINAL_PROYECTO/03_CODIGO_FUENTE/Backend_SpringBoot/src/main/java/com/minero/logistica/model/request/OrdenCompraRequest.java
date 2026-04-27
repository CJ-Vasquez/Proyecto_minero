package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenCompraRequest {
    
    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;
    
    @NotBlank(message = "El destino es obligatorio")
    private String destino;
    
    private String referencia;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "Los detalles son obligatorios")
    private List<DetalleOrdenCompraRequest> detalles;
}
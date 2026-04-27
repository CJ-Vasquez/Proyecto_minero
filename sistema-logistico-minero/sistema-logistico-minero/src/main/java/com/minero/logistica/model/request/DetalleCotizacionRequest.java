package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleCotizacionRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    private Double precioUnitario;
}
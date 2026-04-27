package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleSalidaRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}
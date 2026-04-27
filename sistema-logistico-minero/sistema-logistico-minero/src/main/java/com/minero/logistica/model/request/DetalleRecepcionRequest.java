package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleRecepcionRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad recibida es obligatoria")
    private Integer cantidadRecibida;
    
    private Integer cantidadDefectuosa;
    
    private String observacion;
}
package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetallePedidoRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad solicitada es obligatoria")
    private Integer cantidadSolicitada;
    
    private Double precioReferencial;
}
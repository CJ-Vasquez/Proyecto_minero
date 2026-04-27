package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecepcionRequest {
    
    @NotNull(message = "El ID de orden de compra es obligatorio")
    private Long ordenCompraId;
    
    @NotBlank(message = "El número de guía de remisión es obligatorio")
    private String numeroGuiaRemision;
    
    @NotBlank(message = "El número de factura es obligatorio")
    private String numeroFactura;
    
    @NotNull(message = "La fecha de recepción es obligatoria")
    private LocalDateTime fechaRecepcion;
    
    @NotBlank(message = "El almacén es obligatorio")
    private String almacen;
    
    private String encargado;
    
    @NotNull(message = "Los detalles son obligatorios")
    private List<DetalleRecepcionRequest> detalles;
}
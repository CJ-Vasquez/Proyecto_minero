package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CotizacionRequest {
    
    @NotNull(message = "El ID de solicitud de pedido es obligatorio")
    private Long solicitudPedidoId;
    
    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;
    
    @NotNull(message = "La fecha de cotización es obligatoria")
    private LocalDate fechaCotizacion;
    
    private LocalDate fechaValidez;
    
    @NotNull(message = "Los detalles son obligatorios")
    private List<DetalleCotizacionRequest> detalles;
}
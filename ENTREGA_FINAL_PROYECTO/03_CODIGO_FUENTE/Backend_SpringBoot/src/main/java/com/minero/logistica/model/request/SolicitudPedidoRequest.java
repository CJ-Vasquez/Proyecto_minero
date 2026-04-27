package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SolicitudPedidoRequest {
    
    @NotBlank(message = "El origen es obligatorio")
    private String origen;
    
    @NotBlank(message = "El solicitante es obligatorio")
    private String solicitante;
    
    private String oficina;
    private String glosa;
    
    @NotBlank(message = "El destino es obligatorio")
    private String destino;
    
    private String aprobador;
    
    @NotBlank(message = "El almacén es obligatorio")
    private String almacen;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "Los detalles son obligatorios")
    private List<DetallePedidoRequest> detalles;
}
package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenSalidaRequest {
    
    private String nombreOrden;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "El destino es obligatorio")
    private String trasladarA;
    
    private String operadorAlmacen;
    
    @NotBlank(message = "El almacén de origen es obligatorio")
    private String almacenOrigen;
    
    private String glosa;
    
    @NotNull(message = "Los detalles son obligatorios")
    private List<DetalleSalidaRequest> detalles;
}
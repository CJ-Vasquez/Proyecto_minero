package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProveedorResponse {
    private Long id;
    private String codigo;
    private String razonSocial;
    private String ruc;
    private String nombreContacto;
    private String telefono;
    private String email;
    private String direccion;
    private String estado;
    private Integer prioridad;
    private Double puntajeEvaluacion;
    private LocalDateTime fechaRegistro;
}
package com.minero.logistica.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProveedorRequest {
    private String codigo;
    
    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;
    
    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos")
    private String ruc;
    
    private String nombreContacto;
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    private String email;
    
    private String direccion;
}
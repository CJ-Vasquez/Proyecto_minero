package com.minero.logistica.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoRequest {
    private String codigo;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @NotBlank(message = "La unidad de medida es obligatoria")
    private String unidadMedida;
    
    @NotNull(message = "El precio referencial es obligatorio")
    private Double precioReferencial;
    
    private Integer stockMinimo;
    private String ubicacionFisica;
}
package com.minero.logistica.model.response;

import lombok.Data;

@Data
public class ProductoResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String unidadMedida;
    private Double precioReferencial;
    private Integer stockMinimo;
    private Integer stockActual;
    private String ubicacionFisica;
    private String imagenUrl;
}
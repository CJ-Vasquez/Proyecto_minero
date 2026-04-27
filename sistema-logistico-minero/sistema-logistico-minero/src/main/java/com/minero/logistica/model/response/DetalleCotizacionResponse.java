package com.minero.logistica.model.response;

import lombok.Data;

@Data
public class DetalleCotizacionResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
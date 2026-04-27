package com.minero.logistica.model.response;

import lombok.Data;

@Data
public class DetallePedidoResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidadSolicitada;
    private Integer cantidadAprobada;
    private Double precioReferencial;
}
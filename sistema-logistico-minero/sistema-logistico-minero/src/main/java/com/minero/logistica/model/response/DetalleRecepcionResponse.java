package com.minero.logistica.model.response;

import lombok.Data;

@Data
public class DetalleRecepcionResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidadPedida;
    private Integer cantidadRecibida;
    private Integer cantidadDefectuosa;
    private String estadoProducto;
    private String observacion;
}
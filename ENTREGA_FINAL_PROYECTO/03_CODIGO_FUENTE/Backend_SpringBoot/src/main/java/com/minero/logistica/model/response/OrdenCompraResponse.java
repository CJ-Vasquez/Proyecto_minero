package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenCompraResponse {
    private Long id;
    private String numeroOrden;
    private Long proveedorId;
    private String nombreProveedor;
    private String destino;
    private String referencia;
    private LocalDate fecha;
    private Double montoTotal;
    private String estado;
    private List<DetalleOrdenCompraResponse> detalles;
}
package com.minero.logistica.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecepcionResponse {
    private Long id;
    @JsonProperty("numeroRecepcion")
    private String numeroOI;
    private Long ordenCompraId;
    @JsonProperty("numeroOrden")
    private String numeroOrdenCompra;
    private Long proveedorId;
    private String nombreProveedor;
    private String numeroGuiaRemision;
    private String numeroFactura;
    private LocalDateTime fechaRecepcion;
    private String almacen;
    private String encargado;
    private String estado;
    private List<DetalleRecepcionResponse> detalles;
}
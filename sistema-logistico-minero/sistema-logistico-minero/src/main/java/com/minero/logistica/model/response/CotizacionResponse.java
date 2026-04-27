package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CotizacionResponse {
    private Long id;
    private String numeroCotizacion;
    private Long solicitudPedidoId;
    private String numeroPedido;
    private Long proveedorId;
    private String nombreProveedor;
    private LocalDate fechaCotizacion;
    private LocalDate fechaValidez;
    private Double montoTotal;
    private String estado;
    private List<DetalleCotizacionResponse> detalles;
}
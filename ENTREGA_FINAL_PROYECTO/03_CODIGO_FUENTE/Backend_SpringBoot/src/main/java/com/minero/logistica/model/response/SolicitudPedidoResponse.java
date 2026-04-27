package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SolicitudPedidoResponse {
    private Long id;
    private String numeroPedido;
    private String origen;
    private String solicitante;
    private String oficina;
    private String glosa;
    private String destino;
    private String aprobador;
    private String almacen;
    private LocalDate fecha;
    private String estado;
    private List<DetallePedidoResponse> detalles;
}
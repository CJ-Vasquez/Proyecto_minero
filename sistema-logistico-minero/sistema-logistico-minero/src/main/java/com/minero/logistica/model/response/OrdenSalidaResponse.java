package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenSalidaResponse {
    private Long id;
    private String numeroOS;
    private String nombreOrden;
    private LocalDate fecha;
    private String trasladarA;
    private String operadorAlmacen;
    private String almacenOrigen;
    private String glosa;
    private Double total;
    private String estado;
    private List<DetalleSalidaResponse> detalles;
}
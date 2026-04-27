package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KardexResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private String tipo;           // ENTRADA, SALIDA
    private String documento;      // OI, OS, OC, AJ
    private String numeroDocumento;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String almacen;
    private String usuario;
    private LocalDateTime fechaMovimiento;
}
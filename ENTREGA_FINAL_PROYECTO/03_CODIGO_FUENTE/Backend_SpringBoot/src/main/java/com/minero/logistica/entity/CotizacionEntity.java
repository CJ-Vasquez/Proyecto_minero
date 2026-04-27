package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_cotizacion", unique = true)
    private String numeroCotizacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_pedido_id", nullable = false)
    private SolicitudPedidoEntity solicitudPedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private ProveedorEntity proveedor;
    
    @Column(name = "fecha_cotizacion")
    private LocalDate fechaCotizacion;
    
    @Column(name = "fecha_validez")
    private LocalDate fechaValidez;
    
    @Column(name = "monto_total")
    private Double montoTotal;
    
    private String estado = "PENDIENTE";  // PENDIENTE, APROBADO, RECHAZADO
    
    private String observaciones;
    
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleCotizacionEntity> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (numeroCotizacion == null) {
            numeroCotizacion = "COT-" + System.currentTimeMillis();
        }
    }
}
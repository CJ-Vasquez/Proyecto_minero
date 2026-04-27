package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_pedido_id", nullable = false)
    private SolicitudPedidoEntity solicitudPedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;
    
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;
    
    @Column(name = "cantidad_aprobada")
    private Integer cantidadAprobada;
    
    @Column(name = "precio_referencial")
    private Double precioReferencial;
}
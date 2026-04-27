package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitudes_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPedidoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_pedido", unique = true)
    private String numeroPedido;
    
    private String origen;
    private String solicitante;
    private String oficina;
    
    @Column(length = 500)
    private String glosa;
    
    private String destino;
    private String aprobador;
    private String almacen;
    private LocalDate fecha;
    
    @Column(nullable = false)
    private String estado = "CREADO";  // CREADO, PENDIENTE_APROBACION, APROBADO, RECHAZADO, CANCELADO
    
    private String motivoRechazo;
    
    @OneToMany(mappedBy = "solicitudPedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetallePedidoEntity> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (numeroPedido == null) {
            numeroPedido = "PED-" + System.currentTimeMillis();
        }
    }
}
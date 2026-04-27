package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", unique = true)
    private String numeroOrden;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private ProveedorEntity proveedor;
    
    private String destino;
    private String referencia;
    private LocalDate fecha;
    
    @Column(name = "monto_total")
    private Double montoTotal;
    
    private String estado = "CREADO";  // CREADO, ENVIADO, CANCELADO
    
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleOrdenCompraEntity> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (numeroOrden == null) {
            numeroOrden = "OC-" + System.currentTimeMillis();
        }
    }
}
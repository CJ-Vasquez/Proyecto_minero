package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recepciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_oi", unique = true)
    private String numeroOI;  // Orden de Ingreso
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompraEntity ordenCompra;
    
    @Column(name = "numero_guia_remision")
    private String numeroGuiaRemision;
    
    @Column(name = "numero_factura")
    private String numeroFactura;
    
    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;
    
    private String almacen;
    private String encargado;
    
    private String estado = "REGISTRADO";  // REGISTRADO, PARCIAL, COMPLETADO, ANULADO
    
    @OneToMany(mappedBy = "recepcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleRecepcionEntity> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (numeroOI == null) {
            numeroOI = "OI-" + System.currentTimeMillis();
        }
    }
}
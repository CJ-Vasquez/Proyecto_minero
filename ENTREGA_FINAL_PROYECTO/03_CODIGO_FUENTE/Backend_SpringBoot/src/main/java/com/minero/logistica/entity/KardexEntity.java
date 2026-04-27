package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;
    
    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;
    
    private String tipo;  // ENTRADA, SALIDA
    private String documento;  // OI, OS, OC
    
    @Column(name = "numero_documento")
    private String numeroDocumento;
    
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    
    @Column(name = "stock_anterior")
    private Integer stockAnterior;
    
    @Column(name = "stock_nuevo")
    private Integer stockNuevo;
    
    private String almacen;
    private String usuario;
    
    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
}
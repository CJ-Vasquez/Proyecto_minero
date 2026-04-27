package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalle_recepcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recepcion_id", nullable = false)
    private RecepcionEntity recepcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;
    
    @Column(name = "cantidad_pedida")
    private Integer cantidadPedida;
    
    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;
    
    @Column(name = "cantidad_defectuosa")
    private Integer cantidadDefectuosa = 0;
    
    @Column(name = "estado_producto")
    private String estadoProducto = "BUENO";  // BUENO, DEFECTUOSO, DEVUELTO
    
    private String observacion;
}
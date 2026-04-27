package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_salida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenSalidaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_os", unique = true)
    private String numeroOS;  // Orden de Salida
    
    @Column(name = "nombre_orden")
    private String nombreOrden;
    
    private LocalDate fecha;
    
    @Column(name = "trasladar_a")
    private String trasladarA;
    
    @Column(name = "operador_almacen")
    private String operadorAlmacen;
    
    @Column(name = "almacen_origen")
    private String almacenOrigen;
    
    private String glosa;
    private Double total;
    
    private String estado = "CREADO";  // CREADO, APROBADO, ANULADO
    
    @OneToMany(mappedBy = "ordenSalida", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleSalidaEntity> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (numeroOS == null) {
            numeroOS = "OS-" + System.currentTimeMillis();
        }
    }
}
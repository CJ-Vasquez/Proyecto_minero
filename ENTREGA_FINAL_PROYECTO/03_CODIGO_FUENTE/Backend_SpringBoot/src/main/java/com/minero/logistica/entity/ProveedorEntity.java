package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 20)
    private String codigo;
    
    @Column(nullable = false)
    private String razonSocial;
    
    @Column(unique = true, nullable = false, length = 11)
    private String ruc;
    
    private String nombreContacto;
    private String telefono;
    private String email;
    private String direccion;
    
    @Column(nullable = false)
    private String estado = "ACTIVO";  // ACTIVO, INACTIVO, VETADO
    
    private Integer prioridad;  // 1=A, 2=B, 3=C
    private Double puntajeEvaluacion;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
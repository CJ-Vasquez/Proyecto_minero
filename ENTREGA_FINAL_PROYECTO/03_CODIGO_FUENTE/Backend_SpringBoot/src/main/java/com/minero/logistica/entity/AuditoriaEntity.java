package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String usuario;
    private String accion;
    private String entidad;
    private String detalle;
    private String ip;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }
}
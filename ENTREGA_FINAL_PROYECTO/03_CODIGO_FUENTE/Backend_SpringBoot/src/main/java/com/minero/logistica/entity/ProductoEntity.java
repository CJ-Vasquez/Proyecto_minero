package com.minero.logistica.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 50)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false)
    private String categoria;
    
    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;
    
    @Column(name = "precio_referencial")
    private Double precioReferencial;
    
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;
    
    @Column(name = "stock_actual")
    private Integer stockActual = 0;
    
    @Column(name = "ubicacion_fisica")
    private String ubicacionFisica;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    private Boolean activo = true;
    
 // En ProductoEntity.java
    @Column(name = "stock_lima")
    private Integer stockLima = 0;

    @Column(name = "stock_trujillo")
    private Integer stockTrujillo = 0;

    @Column(name = "stock_mina")
    private Integer stockMina = 0;
}
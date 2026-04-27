package com.minero.logistica.repository;

import com.minero.logistica.entity.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    
    // Búsqueda por RUC
    Optional<ProveedorEntity> findByRuc(String ruc);
    
    // Búsqueda por código
    Optional<ProveedorEntity> findByCodigo(String codigo);
    
    // Verificar existencia por RUC
    boolean existsByRuc(String ruc);
    
    // Listar proveedores activos
    List<ProveedorEntity> findByEstado(String estado);
    
    // Listar proveedores por prioridad
    List<ProveedorEntity> findByPrioridadOrderByPuntajeEvaluacionDesc(Integer prioridad);
    
    // Buscar proveedores por razón social (contiene)
    List<ProveedorEntity> findByRazonSocialContainingIgnoreCase(String razonSocial);
    
    // Proveedores mejor calificados (prioridad A o B)
    @Query("SELECT p FROM ProveedorEntity p WHERE p.estado = 'ACTIVO' AND p.prioridad IN (1, 2) ORDER BY p.puntajeEvaluacion DESC")
    List<ProveedorEntity> findProveedoresPreferentes();
    
    // Proveedores con mejor puntaje para una categoría de producto
    @Query(value = "SELECT p.* FROM proveedores p " +
                   "JOIN cotizaciones c ON p.id = c.proveedor_id " +
                   "JOIN detalle_cotizacion dc ON c.id = dc.cotizacion_id " +
                   "JOIN productos pr ON dc.producto_id = pr.id " +
                   "WHERE pr.categoria = :categoria AND c.estado = 'APROBADO' " +
                   "GROUP BY p.id ORDER BY AVG(p.puntaje_evaluacion) DESC LIMIT 5", 
           nativeQuery = true)
    List<ProveedorEntity> findMejoresProveedoresPorCategoria(@Param("categoria") String categoria);
}
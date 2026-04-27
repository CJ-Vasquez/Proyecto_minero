package com.minero.logistica.repository;

import com.minero.logistica.entity.RecepcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecepcionRepository extends JpaRepository<RecepcionEntity, Long> {
    
    // Búsquedas básicas
    List<RecepcionEntity> findByNumeroOIContainingIgnoreCase(String numeroOI);
    List<RecepcionEntity> findByEstado(String estado);
    List<RecepcionEntity> findByAlmacen(String almacen);
    List<RecepcionEntity> findByOrdenCompraId(Long ordenCompraId);
    List<RecepcionEntity> findByFechaRecepcionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Recepciones pendientes
    @Query("SELECT r FROM RecepcionEntity r WHERE r.estado = 'REGISTRADO' OR r.estado = 'PARCIAL'")
    List<RecepcionEntity> findRecepcionesPendientes();
    
    // Verificar si una orden de compra ya fue recepcionada completamente
    @Query("SELECT COUNT(r) > 0 FROM RecepcionEntity r WHERE r.ordenCompra.id = :ordenCompraId AND r.estado = 'COMPLETADO'")
    boolean isOrdenCompraCompletamenteRecepcionada(@Param("ordenCompraId") Long ordenCompraId);
    
    // Cambiar estado
    @Modifying
    @Transactional
    @Query("UPDATE RecepcionEntity r SET r.estado = :nuevoEstado WHERE r.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);
}
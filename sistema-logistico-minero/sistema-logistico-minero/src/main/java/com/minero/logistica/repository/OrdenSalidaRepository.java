package com.minero.logistica.repository;

import com.minero.logistica.entity.OrdenSalidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenSalidaRepository extends JpaRepository<OrdenSalidaEntity, Long> {
    
    // Búsquedas básicas
    List<OrdenSalidaEntity> findByNumeroOSContainingIgnoreCase(String numeroOS);
    List<OrdenSalidaEntity> findByEstado(String estado);
    List<OrdenSalidaEntity> findByAlmacenOrigen(String almacenOrigen);
    List<OrdenSalidaEntity> findByTrasladarA(String trasladarA);
    List<OrdenSalidaEntity> findByFechaBetween(LocalDate inicio, LocalDate fin);
    
    // Órdenes pendientes de aprobación
    @Query("SELECT o FROM OrdenSalidaEntity o WHERE o.estado = 'CREADO' ORDER BY o.fecha ASC")
    List<OrdenSalidaEntity> findOrdenesPendientesAprobacion();
    
    // Salidas por almacén en rango de fechas
    @Query("SELECT o FROM OrdenSalidaEntity o WHERE o.almacenOrigen = :almacen AND o.fecha BETWEEN :inicio AND :fin")
    List<OrdenSalidaEntity> findByAlmacenOrigenAndFechaBetween(@Param("almacen") String almacen, 
                                                                @Param("inicio") LocalDate inicio, 
                                                                @Param("fin") LocalDate fin);
    
    // Cambiar estado
    @Modifying
    @Transactional
    @Query("UPDATE OrdenSalidaEntity o SET o.estado = :nuevoEstado WHERE o.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);
}
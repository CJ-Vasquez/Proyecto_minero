package com.minero.logistica.repository;

import com.minero.logistica.entity.OrdenCompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompraEntity, Long> {
    
    // Búsquedas básicas
    List<OrdenCompraEntity> findByNumeroOrdenContainingIgnoreCase(String numeroOrden);
    List<OrdenCompraEntity> findByEstado(String estado);
    List<OrdenCompraEntity> findByProveedorId(Long proveedorId);
    List<OrdenCompraEntity> findByFechaBetween(LocalDate inicio, LocalDate fin);
    
    // Órdenes pendientes de envío
    @Query("SELECT o FROM OrdenCompraEntity o WHERE o.estado = 'CREADO' ORDER BY o.fecha ASC")
    List<OrdenCompraEntity> findOrdenesPendientesEnvio();
    
    // Órdenes por proveedor y estado
    List<OrdenCompraEntity> findByProveedorIdAndEstado(Long proveedorId, String estado);
    
    // Órdenes del mes actual
    @Query("SELECT o FROM OrdenCompraEntity o WHERE YEAR(o.fecha) = YEAR(CURRENT_DATE) AND MONTH(o.fecha) = MONTH(CURRENT_DATE)")
    List<OrdenCompraEntity> findOrdenesDelMesActual();
    
    // Total gastado por proveedor
    @Query("SELECT SUM(o.montoTotal) FROM OrdenCompraEntity o WHERE o.proveedor.id = :proveedorId AND o.estado != 'CANCELADO'")
    Double sumMontoTotalByProveedor(@Param("proveedorId") Long proveedorId);
    
    // Cambiar estado
    @Modifying
    @Transactional
    @Query("UPDATE OrdenCompraEntity o SET o.estado = :nuevoEstado WHERE o.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);
}
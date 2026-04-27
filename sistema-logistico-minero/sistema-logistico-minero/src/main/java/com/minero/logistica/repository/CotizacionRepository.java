package com.minero.logistica.repository;

import com.minero.logistica.entity.CotizacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<CotizacionEntity, Long> {
    
    // Búsqueda por número de cotización
    List<CotizacionEntity> findByNumeroCotizacionContainingIgnoreCase(String numeroCotizacion);
    
    // Listar por estado
    List<CotizacionEntity> findByEstado(String estado);
    
    // Listar por proveedor
    List<CotizacionEntity> findByProveedorId(Long proveedorId);
    
    // Listar por solicitud de pedido
    List<CotizacionEntity> findBySolicitudPedidoId(Long solicitudPedidoId);
    
    // Cotizaciones pendientes de aprobación
    @Query("SELECT c FROM CotizacionEntity c WHERE c.estado = 'PENDIENTE' ORDER BY c.fechaCotizacion ASC")
    List<CotizacionEntity> findCotizacionesPendientes();
    
    // Cotizaciones por rango de fechas
    List<CotizacionEntity> findByFechaCotizacionBetween(LocalDate inicio, LocalDate fin);
    
    // Mejor cotización para una solicitud (más baja)
    @Query("SELECT c FROM CotizacionEntity c WHERE c.solicitudPedido.id = :solicitudId AND c.estado = 'APROBADO' ORDER BY c.montoTotal ASC")
    List<CotizacionEntity> findMejoresCotizacionesPorSolicitud(@Param("solicitudId") Long solicitudId);
    
    // Cotizaciones vigentes (fecha validez no expirada)
    @Query("SELECT c FROM CotizacionEntity c WHERE c.fechaValidez >= CURRENT_DATE AND c.estado = 'APROBADO'")
    List<CotizacionEntity> findCotizacionesVigentes();
    
    // Cambiar estado de cotización
    @Modifying
    @Transactional
    @Query("UPDATE CotizacionEntity c SET c.estado = :nuevoEstado WHERE c.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);
}
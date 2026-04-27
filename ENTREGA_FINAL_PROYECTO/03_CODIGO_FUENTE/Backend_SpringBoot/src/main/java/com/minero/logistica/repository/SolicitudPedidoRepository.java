package com.minero.logistica.repository;

import com.minero.logistica.entity.SolicitudPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudPedidoRepository extends JpaRepository<SolicitudPedidoEntity, Long> {
    
    // Búsqueda por número de pedido
    List<SolicitudPedidoEntity> findByNumeroPedidoContainingIgnoreCase(String numeroPedido);
    
    // Listar por estado
    List<SolicitudPedidoEntity> findByEstado(String estado);
    
    // Listar por estado y fecha
    List<SolicitudPedidoEntity> findByEstadoAndFechaBetween(String estado, LocalDate inicio, LocalDate fin);
    
    // Listar por origen (Lima, Trujillo, Mina)
    List<SolicitudPedidoEntity> findByOrigen(String origen);
    
    // Listar por solicitante
    List<SolicitudPedidoEntity> findBySolicitanteContainingIgnoreCase(String solicitante);
    
    // Solicitudes pendientes de aprobación
    @Query("SELECT s FROM SolicitudPedidoEntity s WHERE s.estado = 'PENDIENTE_APROBACION' ORDER BY s.fecha ASC")
    List<SolicitudPedidoEntity> findSolicitudesPendientesAprobacion();
    
    // Solicitudes aprobadas en un rango de fechas
    List<SolicitudPedidoEntity> findByEstadoAndFechaBetweenOrderByFechaDesc(String estado, LocalDate inicio, LocalDate fin);
    
    // Contar solicitudes por estado
    @Query("SELECT s.estado, COUNT(s) FROM SolicitudPedidoEntity s GROUP BY s.estado")
    List<Object[]> countSolicitudesByEstado();
    
    // Cambiar estado de una solicitud
    @Modifying
    @Transactional
    @Query("UPDATE SolicitudPedidoEntity s SET s.estado = :nuevoEstado WHERE s.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);
    
    // Cambiar estado con motivo de rechazo
    @Modifying
    @Transactional
    @Query("UPDATE SolicitudPedidoEntity s SET s.estado = :nuevoEstado, s.motivoRechazo = :motivo WHERE s.id = :id")
    void rechazarSolicitud(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado, @Param("motivo") String motivo);
}
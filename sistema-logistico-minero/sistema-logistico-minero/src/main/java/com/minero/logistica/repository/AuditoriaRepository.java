package com.minero.logistica.repository;

import com.minero.logistica.entity.AuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Long> {
    
    // Auditoría por usuario
    List<AuditoriaEntity> findByUsuarioOrderByFechaHoraDesc(String usuario);
    
    // Auditoría por entidad
    List<AuditoriaEntity> findByEntidadOrderByFechaHoraDesc(String entidad);
    
    // Auditoría por acción
    List<AuditoriaEntity> findByAccion(String accion);
    
    // Auditoría por rango de fechas
    List<AuditoriaEntity> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Auditoría de un usuario en un rango de fechas
    List<AuditoriaEntity> findByUsuarioAndFechaHoraBetweenOrderByFechaHoraDesc(String usuario, LocalDateTime inicio, LocalDateTime fin);
    
    // Últimas N auditorías
    @Query("SELECT a FROM AuditoriaEntity a ORDER BY a.fechaHora DESC LIMIT :limit")
    List<AuditoriaEntity> findUltimasAuditorias(@Param("limit") int limit);
    
    // Auditorías por tipo de entidad y acción
    @Query("SELECT a FROM AuditoriaEntity a WHERE a.entidad = :entidad AND a.accion = :accion ORDER BY a.fechaHora DESC")
    List<AuditoriaEntity> findByEntidadAndAccion(@Param("entidad") String entidad, @Param("accion") String accion);
}
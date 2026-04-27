package com.minero.logistica.repository;

import com.minero.logistica.entity.KardexEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {
    
    // Movimientos por producto (orden descendente)
    List<KardexEntity> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
    
    // Movimientos por tipo
    List<KardexEntity> findByProductoIdAndTipo(Long productoId, String tipo);
    
    // Movimientos por rango de fechas
    List<KardexEntity> findByFechaMovimientoBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Movimientos por almacén
    List<KardexEntity> findByAlmacen(String almacen);
    
    // Movimientos por documento
    List<KardexEntity> findByDocumentoAndNumeroDocumento(String documento, String numeroDocumento);
    
    // Último movimiento de un producto
    @Query("SELECT k FROM KardexEntity k WHERE k.producto.id = :productoId ORDER BY k.fechaMovimiento DESC LIMIT 1")
    KardexEntity findUltimoMovimientoByProducto(@Param("productoId") Long productoId);
    
    // Stock actual de un producto
    @Query("SELECT k.stockNuevo FROM KardexEntity k WHERE k.producto.id = :productoId ORDER BY k.fechaMovimiento DESC LIMIT 1")
    Integer findStockActualByProducto(@Param("productoId") Long productoId);
    
    // Historial por producto y fechas
    @Query("SELECT k FROM KardexEntity k WHERE k.producto.id = :productoId AND k.fechaMovimiento BETWEEN :inicio AND :fin ORDER BY k.fechaMovimiento ASC")
    List<KardexEntity> findHistorialByProductoAndFechas(@Param("productoId") Long productoId,
                                                         @Param("inicio") LocalDateTime inicio,
                                                         @Param("fin") LocalDateTime fin);
    
    // Resumen de movimientos por producto
    @Query("SELECT k.producto.id, " +
           "SUM(CASE WHEN k.tipo = 'ENTRADA' THEN k.cantidad ELSE 0 END) as totalEntradas, " +
           "SUM(CASE WHEN k.tipo = 'SALIDA' THEN k.cantidad ELSE 0 END) as totalSalidas " +
           "FROM KardexEntity k WHERE k.producto.id = :productoId GROUP BY k.producto.id")
    Object[] findResumenMovimientosByProducto(@Param("productoId") Long productoId);
    
    // Últimos movimientos con paginación (reemplaza a findUltimasAuditorias)
    List<KardexEntity> findAllByOrderByFechaMovimientoDesc(Pageable pageable);
}
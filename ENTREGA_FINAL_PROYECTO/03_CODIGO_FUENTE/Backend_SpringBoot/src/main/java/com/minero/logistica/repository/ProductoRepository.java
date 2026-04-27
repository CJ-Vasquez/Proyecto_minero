package com.minero.logistica.repository;

import com.minero.logistica.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    
    Optional<ProductoEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<ProductoEntity> findByActivoTrue();
    List<ProductoEntity> findByCategoria(String categoria);
    
    // Stock crítico
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true AND p.stockActual <= p.stockMinimo")
    List<ProductoEntity> findProductosConStockCritico();
    
    // Sin stock
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true AND p.stockActual = 0")
    List<ProductoEntity> findProductosSinStock();
    
    // Búsqueda por nombre o descripción
    List<ProductoEntity> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
    
    // Actualizar stock
    @Modifying
    @Transactional
    @Query("UPDATE ProductoEntity p SET p.stockActual = p.stockActual + :cantidad WHERE p.id = :productoId")
    void actualizarStock(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad);
    
    // Contar por categoría
    @Query("SELECT p.categoria, COUNT(p) FROM ProductoEntity p WHERE p.activo = true GROUP BY p.categoria")
    List<Object[]> countProductosByCategoria();
}
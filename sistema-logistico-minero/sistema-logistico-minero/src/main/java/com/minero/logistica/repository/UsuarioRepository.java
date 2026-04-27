package com.minero.logistica.repository;

import com.minero.logistica.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    // Búsqueda por username
    Optional<UsuarioEntity> findByUsername(String username);
    
    // Búsqueda por email
    Optional<UsuarioEntity> findByEmail(String email);
    
    // Verificar existencia por username
    boolean existsByUsername(String username);
    
    // Verificar existencia por email
    boolean existsByEmail(String email);
    
    // Listar usuarios activos
    List<UsuarioEntity> findByActivoTrue();
    
    // Listar usuarios por rol
    List<UsuarioEntity> findByRol(String rol);
    
    // Listar usuarios bloqueados
    List<UsuarioEntity> findByBloqueadoTrue();
    
    // Buscar por username o email (para login)
    Optional<UsuarioEntity> findByUsernameOrEmail(String username, String email);
    
    // Incrementar intentos fallidos
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.username = :username")
    void incrementarIntentosFallidos(@Param("username") String username);
    
    // Resetear intentos fallidos
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.intentosFallidos = 0 WHERE u.username = :username")
    void resetearIntentosFallidos(@Param("username") String username);
    
    // Bloquear usuario
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.bloqueado = true WHERE u.username = :username")
    void bloquearUsuario(@Param("username") String username);
    
    // Actualizar último acceso
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.ultimoAcceso = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
    
    // Usuarios con contraseña por expirar (más de 30 días sin cambio)
    @Query("SELECT u FROM UsuarioEntity u WHERE u.ultimoCambioPassword <= :fechaLimite")
    List<UsuarioEntity> findUsuariosConPasswordPorExpiracion(@Param("fechaLimite") LocalDateTime fechaLimite);
}
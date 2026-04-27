package com.minero.logistica.service;

import com.minero.logistica.entity.UsuarioEntity;
import com.minero.logistica.model.request.UsuarioRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.UsuarioResponse;
import com.minero.logistica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    
    /**
     * Registrar nuevo usuario (RFG_02 - Administrar Usuarios)
     */
    @Transactional
    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getUsername());
        
        // Verificar si ya existe el username
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        // Verificar si ya existe el email
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCargo(request.getCargo());
        usuario.setRol(request.getRol() != null ? request.getRol() : "ASISTENTE_ALMACEN");
        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setUltimoCambioPassword(LocalDateTime.now());
        
        UsuarioEntity saved = usuarioRepository.save(usuario);
        
        auditoriaService.registrar("CREAR_USUARIO", "USUARIO", 
            "Usuario creado: " + saved.getUsername() + " con rol: " + saved.getRol());
        
        return convertToResponse(saved);
    }
    
    /**
     * Listar todos los usuarios
     */
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener usuario por ID
     */
    public UsuarioResponse obtenerUsuario(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToResponse(usuario);
    }
    
    /**
     * Actualizar usuario
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (request.getNombres() != null) usuario.setNombres(request.getNombres());
        if (request.getApellidos() != null) usuario.setApellidos(request.getApellidos());
        if (request.getEmail() != null) usuario.setEmail(request.getEmail());
        if (request.getCargo() != null) usuario.setCargo(request.getCargo());
        if (request.getRol() != null) usuario.setRol(request.getRol());
        
        UsuarioEntity updated = usuarioRepository.save(usuario);
        
        auditoriaService.registrar("ACTUALIZAR_USUARIO", "USUARIO", 
            "Usuario actualizado: " + updated.getUsername());
        
        return convertToResponse(updated);
    }
    
    /**
     * Activar/Desactivar usuario
     */
    @Transactional
    public ApiResponse<?> cambiarEstadoUsuario(Long id, Boolean activo) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
        
        auditoriaService.registrar("CAMBIAR_ESTADO_USUARIO", "USUARIO", 
            "Usuario " + usuario.getUsername() + " estado: " + (activo ? "ACTIVO" : "INACTIVO"));
        
        return ApiResponse.success(null, "Estado del usuario actualizado");
    }
    
    /**
     * Eliminar usuario
     */
    @Transactional
    public ApiResponse<?> eliminarUsuario(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioRepository.delete(usuario);

        auditoriaService.registrar("ELIMINAR_USUARIO", "USUARIO",
            "Usuario eliminado: " + usuario.getUsername());

        return ApiResponse.success(null, "Usuario eliminado correctamente");
    }

    /**
     * Cambiar contraseña de un usuario (cifrada con BCrypt)
     */
    @Transactional
    public ApiResponse<?> cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuario.setUltimoCambioPassword(LocalDateTime.now());
        usuarioRepository.save(usuario);

        auditoriaService.registrar("CAMBIAR_PASSWORD", "USUARIO",
            "Cambio de contraseña para usuario: " + usuario.getUsername());

        return ApiResponse.success(null, "Contraseña actualizada correctamente");
    }
    
    private UsuarioResponse convertToResponse(UsuarioEntity entity) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setEmail(entity.getEmail());
        response.setNombres(entity.getNombres());
        response.setApellidos(entity.getApellidos());
        response.setCargo(entity.getCargo());
        response.setRol(entity.getRol());
        response.setActivo(entity.getActivo());
        response.setFechaCreacion(entity.getFechaCreacion());
        response.setUltimoAcceso(entity.getUltimoAcceso());
        return response;
    }
}
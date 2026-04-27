package com.minero.logistica.service;

import com.minero.logistica.entity.AuditoriaEntity;
import com.minero.logistica.repository.AuditoriaRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;
    private final HttpServletRequest request;
    
    /**
     * Registrar acción en auditoría (RNF_57)
     */
    public void registrar(String accion, String entidad, String detalle) {
        try {
            String username = obtenerUsuarioActual();
            
            AuditoriaEntity auditoria = new AuditoriaEntity();
            auditoria.setUsuario(username);
            auditoria.setAccion(accion);
            auditoria.setEntidad(entidad);
            auditoria.setDetalle(detalle);
            auditoria.setIp(obtenerIPCliente());
            auditoria.setFechaHora(LocalDateTime.now());
            
            auditoriaRepository.save(auditoria);
            log.debug("Auditoría registrada: {} - {} - {}", accion, entidad, username);
        } catch (Exception e) {
            log.error("Error al registrar auditoría: {}", e.getMessage());
        }
    }
    
    /**
     * Obtener usuario actual del contexto de seguridad
     */
    private String obtenerUsuarioActual() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "SISTEMA";
        }
    }
    
    /**
     * Obtener IP del cliente
     */
    private String obtenerIPCliente() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
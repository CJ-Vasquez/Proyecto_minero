package com.minero.logistica.service;

import com.minero.logistica.entity.ProveedorEntity;
import com.minero.logistica.model.request.ProveedorRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.ProveedorResponse;
import com.minero.logistica.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorService {
    
    private final ProveedorRepository proveedorRepository;
    private final AuditoriaService auditoriaService;
    
    /**
     * Registrar nuevo proveedor (CUS09 - Gestionar Proveedores)
     */
    @Transactional
    public ProveedorResponse registrarProveedor(ProveedorRequest request) {
        log.info("Registrando nuevo proveedor: {}", request.getRazonSocial());
        
        // Verificar si ya existe el RUC
        if (proveedorRepository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe un proveedor con el RUC: " + request.getRuc());
        }
        
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setCodigo(generarCodigoProveedor());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setRuc(request.getRuc());
        proveedor.setNombreContacto(request.getNombreContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setEstado("ACTIVO");
        proveedor.setPrioridad(3); // Prioridad C por defecto
        proveedor.setPuntajeEvaluacion(0.0);
        proveedor.setFechaRegistro(LocalDateTime.now());
        
        ProveedorEntity saved = proveedorRepository.save(proveedor);
        
        auditoriaService.registrar("CREAR_PROVEEDOR", "PROVEEDOR", 
            "Proveedor creado: " + saved.getRazonSocial() + " - RUC: " + saved.getRuc());
        
        return convertToResponse(saved);
    }
    
    /**
     * Listar todos los proveedores
     */
    public List<ProveedorResponse> listarProveedores() {
        return proveedorRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar proveedores activos
     */
    public List<ProveedorResponse> listarProveedoresActivos() {
        return proveedorRepository.findByEstado("ACTIVO").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar proveedores preferentes (prioridad A o B)
     */
    public List<ProveedorResponse> listarProveedoresPreferentes() {
        return proveedorRepository.findProveedoresPreferentes().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener proveedor por ID
     */
    public ProveedorResponse obtenerProveedor(Long id) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        return convertToResponse(proveedor);
    }
    
    /**
     * Actualizar proveedor
     */
    @Transactional
    public ProveedorResponse actualizarProveedor(Long id, ProveedorRequest request) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        if (request.getRazonSocial() != null) proveedor.setRazonSocial(request.getRazonSocial());
        if (request.getNombreContacto() != null) proveedor.setNombreContacto(request.getNombreContacto());
        if (request.getTelefono() != null) proveedor.setTelefono(request.getTelefono());
        if (request.getEmail() != null) proveedor.setEmail(request.getEmail());
        if (request.getDireccion() != null) proveedor.setDireccion(request.getDireccion());
        
        ProveedorEntity updated = proveedorRepository.save(proveedor);
        
        auditoriaService.registrar("ACTUALIZAR_PROVEEDOR", "PROVEEDOR", 
            "Proveedor actualizado: " + updated.getRazonSocial());
        
        return convertToResponse(updated);
    }
    
    /**
     * Evaluar proveedor (CUS09 - Evaluación de proveedores)
     * Puntajes: 48-40 = Prioridad A, 39-36 = Prioridad B, 35-30 = Prioridad C
     */
    @Transactional
    public ApiResponse<?> evaluarProveedor(Long id, Double puntaje) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        proveedor.setPuntajeEvaluacion(puntaje);
        
        if (puntaje >= 40) {
            proveedor.setPrioridad(1); // Prioridad A
        } else if (puntaje >= 36) {
            proveedor.setPrioridad(2); // Prioridad B
        } else if (puntaje >= 30) {
            proveedor.setPrioridad(3); // Prioridad C
        } else {
            proveedor.setEstado("INACTIVO"); // Dar de baja
            proveedor.setPrioridad(0);
            auditoriaService.registrar("PROVEEDOR_DADO_BAJA", "PROVEEDOR", 
                "Proveedor dado de baja por bajo puntaje: " + proveedor.getRazonSocial());
        }
        
        proveedorRepository.save(proveedor);
        
        String prioridadTexto = proveedor.getPrioridad() == 1 ? "A" : 
                                (proveedor.getPrioridad() == 2 ? "B" : "C");
        
        auditoriaService.registrar("EVALUAR_PROVEEDOR", "PROVEEDOR", 
            "Proveedor evaluado: " + proveedor.getRazonSocial() + 
            " - Puntaje: " + puntaje + " - Prioridad: " + prioridadTexto);
        
        return ApiResponse.success(null, "Proveedor evaluado correctamente. Prioridad: " + prioridadTexto);
    }
    
    /**
     * Cambiar estado del proveedor (Activo/Inactivo/Vetado)
     */
    @Transactional
    public ApiResponse<?> cambiarEstadoProveedor(Long id, String estado) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        proveedor.setEstado(estado);
        proveedorRepository.save(proveedor);
        
        auditoriaService.registrar("CAMBIAR_ESTADO_PROVEEDOR", "PROVEEDOR", 
            "Proveedor " + proveedor.getRazonSocial() + " estado: " + estado);
        
        return ApiResponse.success(null, "Estado del proveedor actualizado a: " + estado);
    }
    
    /**
     * Eliminar proveedor
     */
    @Transactional
    public ApiResponse<?> eliminarProveedor(Long id) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        proveedorRepository.delete(proveedor);
        
        auditoriaService.registrar("ELIMINAR_PROVEEDOR", "PROVEEDOR", 
            "Proveedor eliminado: " + proveedor.getRazonSocial());
        
        return ApiResponse.success(null, "Proveedor eliminado correctamente");
    }
    
    /**
     * Generar código único para proveedor
     */
    private String generarCodigoProveedor() {
        long count = proveedorRepository.count() + 1;
        return String.format("PROV-%05d", count);
    }
    
    private ProveedorResponse convertToResponse(ProveedorEntity entity) {
        ProveedorResponse response = new ProveedorResponse();
        response.setId(entity.getId());
        response.setCodigo(entity.getCodigo());
        response.setRazonSocial(entity.getRazonSocial());
        response.setRuc(entity.getRuc());
        response.setNombreContacto(entity.getNombreContacto());
        response.setTelefono(entity.getTelefono());
        response.setEmail(entity.getEmail());
        response.setDireccion(entity.getDireccion());
        response.setEstado(entity.getEstado());
        response.setPrioridad(entity.getPrioridad());
        response.setPuntajeEvaluacion(entity.getPuntajeEvaluacion());
        response.setFechaRegistro(entity.getFechaRegistro());
        return response;
    }
}
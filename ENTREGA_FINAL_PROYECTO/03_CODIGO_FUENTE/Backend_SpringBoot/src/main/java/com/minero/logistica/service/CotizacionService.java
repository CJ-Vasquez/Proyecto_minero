package com.minero.logistica.service;

import com.minero.logistica.entity.*;
import com.minero.logistica.model.request.CotizacionRequest;
import com.minero.logistica.model.request.DetalleCotizacionRequest;
import com.minero.logistica.model.response.CotizacionResponse;
import com.minero.logistica.model.response.DetalleCotizacionResponse;
import com.minero.logistica.repository.CotizacionRepository;
import com.minero.logistica.repository.ProductoRepository;
import com.minero.logistica.repository.ProveedorRepository;
import com.minero.logistica.repository.SolicitudPedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final SolicitudPedidoRepository solicitudPedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public CotizacionResponse crearCotizacion(CotizacionRequest request) {
        log.info("Creando cotización para solicitud: {}", request.getSolicitudPedidoId());
        
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(request.getSolicitudPedidoId())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        ProveedorEntity proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        CotizacionEntity cotizacion = new CotizacionEntity();
        cotizacion.setSolicitudPedido(solicitud);
        cotizacion.setProveedor(proveedor);
        cotizacion.setFechaCotizacion(request.getFechaCotizacion());
        cotizacion.setFechaValidez(request.getFechaValidez());
        cotizacion.setEstado("PENDIENTE");
        
        List<DetalleCotizacionEntity> detalles = new ArrayList<>();
        double montoTotal = 0.0;
        
        for (DetalleCotizacionRequest detalleReq : request.getDetalles()) {
            ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            DetalleCotizacionEntity detalle = new DetalleCotizacionEntity();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleReq.getCantidad());
            detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
            detalle.setSubtotal(detalleReq.getCantidad() * detalleReq.getPrecioUnitario());
            detalle.setCotizacion(cotizacion);
            detalles.add(detalle);
            
            montoTotal += detalle.getSubtotal();
        }
        
        cotizacion.setDetalles(detalles);
        cotizacion.setMontoTotal(montoTotal);
        
        CotizacionEntity saved = cotizacionRepository.save(cotizacion);
        return convertToResponse(saved);
    }
    
    /**
     * Listar todas las cotizaciones
     */
    public List<CotizacionResponse> listarCotizaciones() {
        return cotizacionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cotizaciones pendientes de aprobación
     */
    public List<CotizacionResponse> listarPendientesAprobacion() {
        return cotizacionRepository.findByEstado("PENDIENTE").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cotizaciones vigentes (fecha validez no expirada)
     */
    public List<CotizacionResponse> listarVigentes() {
        return cotizacionRepository.findAll().stream()
                .filter(c -> c.getFechaValidez() != null && !c.getFechaValidez().isBefore(LocalDate.now()))
                .filter(c -> "APROBADO".equals(c.getEstado()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cotizaciones por solicitud de pedido
     */
    public List<CotizacionResponse> listarPorSolicitudPedido(Long solicitudPedidoId) {
        return cotizacionRepository.findBySolicitudPedidoId(solicitudPedidoId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener cotización por ID
     */
    public CotizacionResponse obtenerCotizacion(Long id) {
        CotizacionEntity cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        return convertToResponse(cotizacion);
    }
    
    /**
     * Enviar cotización a proveedor (simula envío de email)
     */
    @Transactional
    public void enviarAProveedor(Long id) {
        CotizacionEntity cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        log.info("Enviando cotización {} al proveedor {}", 
            cotizacion.getNumeroCotizacion(), 
            cotizacion.getProveedor().getRazonSocial());
        
        cotizacion.setEstado("ENVIADA");
        cotizacionRepository.save(cotizacion);
    }
    
    /**
     * Recibir respuesta del proveedor a la cotización
     * @param id ID de la cotización
     * @param precioTotal Precio total ofertado por el proveedor
     * @param observaciones Observaciones adicionales
     * @return Cotización actualizada
     */
    @Transactional
    public CotizacionResponse recibirRespuestaProveedor(Long id, Double precioTotal, String observaciones) {
        log.info("Recibiendo respuesta de proveedor para cotización: {}", id);
        
        CotizacionEntity cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + id));
        
        // Actualizar precio total si se proporciona
        if (precioTotal != null) {
            cotizacion.setMontoTotal(precioTotal);
            log.info("Precio total actualizado a: {}", precioTotal);
        }
        
        // Actualizar observaciones
        if (observaciones != null && !observaciones.isEmpty()) {
            cotizacion.setObservaciones(observaciones);
            log.info("Observaciones agregadas: {}", observaciones);
        }
        
        // Cambiar estado a "RESPONDIDA"
        cotizacion.setEstado("RESPONDIDA");
        
        CotizacionEntity saved = cotizacionRepository.save(cotizacion);
        
        log.info("Respuesta de proveedor registrada para cotización: {}", saved.getNumeroCotizacion());
        
        return convertToResponse(saved);
    }
    
    /**
     * Aprobar cotización
     */
    @Transactional
    public CotizacionResponse aprobarCotizacion(Long id) {
        CotizacionEntity cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        cotizacion.setEstado("APROBADO");
        CotizacionEntity saved = cotizacionRepository.save(cotizacion);
        return convertToResponse(saved);
    }
    
    /**
     * Rechazar cotización
     */
    @Transactional
    public CotizacionResponse rechazarCotizacion(Long id, String motivo) {
        CotizacionEntity cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        cotizacion.setEstado("RECHAZADO");
        cotizacion.setObservaciones(motivo);
        CotizacionEntity saved = cotizacionRepository.save(cotizacion);
        return convertToResponse(saved);
    }
    
    private CotizacionResponse convertToResponse(CotizacionEntity entity) {
        CotizacionResponse response = new CotizacionResponse();
        response.setId(entity.getId());
        response.setNumeroCotizacion(entity.getNumeroCotizacion());
        response.setSolicitudPedidoId(entity.getSolicitudPedido().getId());
        response.setNumeroPedido(entity.getSolicitudPedido().getNumeroPedido());
        response.setProveedorId(entity.getProveedor().getId());
        response.setNombreProveedor(entity.getProveedor().getRazonSocial());
        response.setFechaCotizacion(entity.getFechaCotizacion());
        response.setFechaValidez(entity.getFechaValidez());
        response.setMontoTotal(entity.getMontoTotal());
        response.setEstado(entity.getEstado());
        
        if (entity.getDetalles() != null) {
            List<DetalleCotizacionResponse> detalles = entity.getDetalles().stream()
                    .map(this::convertDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detalles);
        }
        
        return response;
    }
    
    private DetalleCotizacionResponse convertDetalleToResponse(DetalleCotizacionEntity entity) {
        DetalleCotizacionResponse response = new DetalleCotizacionResponse();
        response.setId(entity.getId());
        response.setProductoId(entity.getProducto().getId());
        response.setCodigoProducto(entity.getProducto().getCodigo());
        response.setNombreProducto(entity.getProducto().getNombre());
        response.setCantidad(entity.getCantidad());
        response.setPrecioUnitario(entity.getPrecioUnitario());
        response.setSubtotal(entity.getSubtotal());
        return response;
    }
}
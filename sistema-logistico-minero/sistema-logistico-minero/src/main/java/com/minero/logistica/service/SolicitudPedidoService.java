package com.minero.logistica.service;

import com.minero.logistica.entity.*;
import com.minero.logistica.model.request.SolicitudPedidoRequest;
import com.minero.logistica.model.request.DetallePedidoRequest;
import com.minero.logistica.model.response.SolicitudPedidoResponse;
import com.minero.logistica.model.response.DetallePedidoResponse;
import com.minero.logistica.repository.SolicitudPedidoRepository;
import com.minero.logistica.repository.ProductoRepository;
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
public class SolicitudPedidoService {

    private final SolicitudPedidoRepository solicitudPedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public SolicitudPedidoResponse crearSolicitud(SolicitudPedidoRequest request) {
        log.info("Creando solicitud de pedido desde: {}", request.getOrigen());
        
        SolicitudPedidoEntity solicitud = new SolicitudPedidoEntity();
        solicitud.setOrigen(request.getOrigen());
        solicitud.setSolicitante(request.getSolicitante());
        solicitud.setOficina(request.getOficina());
        solicitud.setGlosa(request.getGlosa());
        solicitud.setDestino(request.getDestino());
        solicitud.setAprobador(request.getAprobador());
        solicitud.setAlmacen(request.getAlmacen());
        solicitud.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        solicitud.setEstado("CREADO");
        
        List<DetallePedidoEntity> detalles = new ArrayList<>();
        if (request.getDetalles() != null) {
            for (DetallePedidoRequest detalleReq : request.getDetalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleReq.getProductoId()));
                
                DetallePedidoEntity detalle = new DetallePedidoEntity();
                detalle.setProducto(producto);
                detalle.setCantidadSolicitada(detalleReq.getCantidadSolicitada());
                detalle.setPrecioReferencial(detalleReq.getPrecioReferencial());
                detalle.setSolicitudPedido(solicitud);
                detalles.add(detalle);
            }
        }
        solicitud.setDetalles(detalles);
        
        SolicitudPedidoEntity saved = solicitudPedidoRepository.save(solicitud);
        return convertToResponse(saved);
    }
    
    @Transactional
    public SolicitudPedidoResponse enviarAAprobacion(Long id) {
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado("PENDIENTE_APROBACION");
        SolicitudPedidoEntity saved = solicitudPedidoRepository.save(solicitud);
        return convertToResponse(saved);
    }
    
    @Transactional
    public SolicitudPedidoResponse aprobarSolicitud(Long id, Integer cantidadAprobada) {
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado("APROBADO");
        
        if (cantidadAprobada != null && solicitud.getDetalles() != null) {
            for (DetallePedidoEntity detalle : solicitud.getDetalles()) {
                detalle.setCantidadAprobada(cantidadAprobada);
            }
        }
        
        SolicitudPedidoEntity saved = solicitudPedidoRepository.save(solicitud);
        return convertToResponse(saved);
    }
    
    @Transactional
    public SolicitudPedidoResponse rechazarSolicitud(Long id, String motivo) {
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado("RECHAZADO");
        solicitud.setMotivoRechazo(motivo);
        SolicitudPedidoEntity saved = solicitudPedidoRepository.save(solicitud);
        return convertToResponse(saved);
    }
    
    @Transactional
    public SolicitudPedidoResponse cancelarSolicitud(Long id) {
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado("CANCELADO");
        SolicitudPedidoEntity saved = solicitudPedidoRepository.save(solicitud);
        return convertToResponse(saved);
    }
    
    public List<SolicitudPedidoResponse> listarSolicitudes() {
        return solicitudPedidoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SolicitudPedidoResponse> listarPorEstado(String estado) {
        return solicitudPedidoRepository.findByEstado(estado).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SolicitudPedidoResponse> listarPendientesAprobacion() {
        return solicitudPedidoRepository.findByEstado("PENDIENTE_APROBACION").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public SolicitudPedidoResponse obtenerSolicitud(Long id) {
        SolicitudPedidoEntity solicitud = solicitudPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return convertToResponse(solicitud);
    }
    
    private SolicitudPedidoResponse convertToResponse(SolicitudPedidoEntity entity) {
        if (entity == null) return null;
        
        SolicitudPedidoResponse response = new SolicitudPedidoResponse();
        response.setId(entity.getId());
        response.setNumeroPedido(entity.getNumeroPedido());
        response.setOrigen(entity.getOrigen());
        response.setSolicitante(entity.getSolicitante());
        response.setOficina(entity.getOficina());
        response.setGlosa(entity.getGlosa());
        response.setDestino(entity.getDestino());
        response.setAprobador(entity.getAprobador());
        response.setAlmacen(entity.getAlmacen());
        response.setFecha(entity.getFecha());
        response.setEstado(entity.getEstado());
        
        if (entity.getDetalles() != null) {
            List<DetallePedidoResponse> detalles = entity.getDetalles().stream()
                    .map(this::convertDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detalles);
        }
        
        return response;
    }
    
    private DetallePedidoResponse convertDetalleToResponse(DetallePedidoEntity entity) {
        DetallePedidoResponse response = new DetallePedidoResponse();
        response.setId(entity.getId());
        response.setProductoId(entity.getProducto().getId());
        response.setCodigoProducto(entity.getProducto().getCodigo());
        response.setNombreProducto(entity.getProducto().getNombre());
        response.setCantidadSolicitada(entity.getCantidadSolicitada());
        response.setCantidadAprobada(entity.getCantidadAprobada());
        response.setPrecioReferencial(entity.getPrecioReferencial());
        return response;
    }
}
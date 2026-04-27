package com.minero.logistica.service;

import com.minero.logistica.entity.*;
import com.minero.logistica.model.request.RecepcionRequest;
import com.minero.logistica.model.request.DetalleRecepcionRequest;
import com.minero.logistica.model.response.RecepcionResponse;
import com.minero.logistica.model.response.DetalleRecepcionResponse;
import com.minero.logistica.repository.RecepcionRepository;
import com.minero.logistica.repository.OrdenCompraRepository;
import com.minero.logistica.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public RecepcionResponse registrarRecepcion(RecepcionRequest request) {
        log.info("Registrando recepción para orden de compra: {}", request.getOrdenCompraId());
        
        OrdenCompraEntity ordenCompra = ordenCompraRepository.findById(request.getOrdenCompraId())
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
        
        RecepcionEntity recepcion = new RecepcionEntity();
        recepcion.setOrdenCompra(ordenCompra);
        recepcion.setNumeroGuiaRemision(request.getNumeroGuiaRemision());
        recepcion.setNumeroFactura(request.getNumeroFactura());
        recepcion.setFechaRecepcion(request.getFechaRecepcion() != null ? request.getFechaRecepcion() : LocalDateTime.now());
        recepcion.setAlmacen(request.getAlmacen());
        recepcion.setEncargado(request.getEncargado());
        recepcion.setEstado("REGISTRADO");
        
        List<DetalleRecepcionEntity> detalles = new ArrayList<>();
        boolean tieneDefectuosos = false;
        
        if (request.getDetalles() != null) {
            for (DetalleRecepcionRequest detalleReq : request.getDetalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                Integer cantidadPedida = obtenerCantidadPedida(ordenCompra, producto.getId());
                
                DetalleRecepcionEntity detalle = new DetalleRecepcionEntity();
                detalle.setProducto(producto);
                detalle.setCantidadPedida(cantidadPedida);
                detalle.setCantidadRecibida(detalleReq.getCantidadRecibida());
                detalle.setCantidadDefectuosa(detalleReq.getCantidadDefectuosa() != null ? detalleReq.getCantidadDefectuosa() : 0);
                detalle.setObservacion(detalleReq.getObservacion());
                detalle.setRecepcion(recepcion);
                
                if (detalle.getCantidadDefectuosa() > 0) {
                    detalle.setEstadoProducto("DEFECTUOSO");
                    tieneDefectuosos = true;
                } else {
                    detalle.setEstadoProducto("BUENO");
                }
                
                detalles.add(detalle);
                
                // Actualizar stock
                int cantidadBuena = detalleReq.getCantidadRecibida() - detalle.getCantidadDefectuosa();
                if (cantidadBuena > 0) {
                    producto.setStockActual(producto.getStockActual() + cantidadBuena);
                    productoRepository.save(producto);
                }
            }
        }
        
        recepcion.setDetalles(detalles);
        
        if (tieneDefectuosos) {
            recepcion.setEstado("PARCIAL");
        } else {
            recepcion.setEstado("COMPLETADO");
        }
        
        RecepcionEntity saved = recepcionRepository.save(recepcion);
        return convertToResponse(saved);
    }
    
    private Integer obtenerCantidadPedida(OrdenCompraEntity ordenCompra, Long productoId) {
        return ordenCompra.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(productoId))
                .map(DetalleOrdenCompraEntity::getCantidad)
                .findFirst()
                .orElse(0);
    }
    
    public List<RecepcionResponse> listarRecepciones() {
        return recepcionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RecepcionResponse> listarRecepcionesPendientes() {
        return recepcionRepository.findByEstado("REGISTRADO").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RecepcionResponse> listarPorAlmacen(String almacen) {
        return recepcionRepository.findByAlmacen(almacen).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RecepcionResponse> listarPorOrdenCompra(Long ordenCompraId) {
        return recepcionRepository.findByOrdenCompraId(ordenCompraId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public RecepcionResponse obtenerRecepcion(Long id) {
        RecepcionEntity recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada"));
        return convertToResponse(recepcion);
    }
    
    @Transactional
    public RecepcionResponse completarRecepcion(Long id) {
        RecepcionEntity recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada"));
        recepcion.setEstado("COMPLETADO");
        RecepcionEntity saved = recepcionRepository.save(recepcion);
        return convertToResponse(saved);
    }
    
    @Transactional
    public RecepcionResponse anularRecepcion(Long id) {
        RecepcionEntity recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada"));
        
        // Revertir stock
        for (DetalleRecepcionEntity detalle : recepcion.getDetalles()) {
            if ("BUENO".equals(detalle.getEstadoProducto())) {
                ProductoEntity producto = detalle.getProducto();
                producto.setStockActual(producto.getStockActual() - detalle.getCantidadRecibida());
                productoRepository.save(producto);
            }
        }
        
        recepcion.setEstado("ANULADO");
        RecepcionEntity saved = recepcionRepository.save(recepcion);
        return convertToResponse(saved);
    }
    
    private RecepcionResponse convertToResponse(RecepcionEntity entity) {
        if (entity == null) return null;
        
        RecepcionResponse response = new RecepcionResponse();
        response.setId(entity.getId());
        response.setNumeroOI(entity.getNumeroOI());
        response.setOrdenCompraId(entity.getOrdenCompra().getId());
        response.setNumeroOrdenCompra(entity.getOrdenCompra().getNumeroOrden());
        response.setProveedorId(entity.getOrdenCompra().getProveedor().getId());
        response.setNombreProveedor(entity.getOrdenCompra().getProveedor().getRazonSocial());
        response.setNumeroGuiaRemision(entity.getNumeroGuiaRemision());
        response.setNumeroFactura(entity.getNumeroFactura());
        response.setFechaRecepcion(entity.getFechaRecepcion());
        response.setAlmacen(entity.getAlmacen());
        response.setEncargado(entity.getEncargado());
        response.setEstado(entity.getEstado());
        
        if (entity.getDetalles() != null) {
            List<DetalleRecepcionResponse> detalles = entity.getDetalles().stream()
                    .map(this::convertDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detalles);
        }
        
        return response;
    }
    
    private DetalleRecepcionResponse convertDetalleToResponse(DetalleRecepcionEntity entity) {
        DetalleRecepcionResponse response = new DetalleRecepcionResponse();
        response.setId(entity.getId());
        response.setProductoId(entity.getProducto().getId());
        response.setCodigoProducto(entity.getProducto().getCodigo());
        response.setNombreProducto(entity.getProducto().getNombre());
        response.setCantidadPedida(entity.getCantidadPedida());
        response.setCantidadRecibida(entity.getCantidadRecibida());
        response.setCantidadDefectuosa(entity.getCantidadDefectuosa());
        response.setEstadoProducto(entity.getEstadoProducto());
        response.setObservacion(entity.getObservacion());
        return response;
    }
}
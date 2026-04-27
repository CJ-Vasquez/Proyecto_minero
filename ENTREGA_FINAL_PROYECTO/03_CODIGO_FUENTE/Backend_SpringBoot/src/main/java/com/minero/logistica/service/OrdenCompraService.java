package com.minero.logistica.service;

import com.minero.logistica.entity.*;
import com.minero.logistica.model.request.OrdenCompraRequest;
import com.minero.logistica.model.request.DetalleOrdenCompraRequest;
import com.minero.logistica.model.response.OrdenCompraResponse;
import com.minero.logistica.model.response.DetalleOrdenCompraResponse;
import com.minero.logistica.repository.OrdenCompraRepository;
import com.minero.logistica.repository.ProductoRepository;
import com.minero.logistica.repository.ProveedorRepository;
import com.minero.logistica.repository.CotizacionRepository;
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
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final CotizacionRepository cotizacionRepository;

    @Transactional
    public OrdenCompraResponse crearOrdenCompra(OrdenCompraRequest request) {
        log.info("Creando orden de compra para proveedor: {}", request.getProveedorId());
        
        ProveedorEntity proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        OrdenCompraEntity ordenCompra = new OrdenCompraEntity();
        ordenCompra.setProveedor(proveedor);
        ordenCompra.setDestino(request.getDestino());
        ordenCompra.setReferencia(request.getReferencia());
        ordenCompra.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        ordenCompra.setEstado("CREADO");
        
        List<DetalleOrdenCompraEntity> detalles = new ArrayList<>();
        double montoTotal = 0.0;
        
        if (request.getDetalles() != null) {
            for (DetalleOrdenCompraRequest detalleReq : request.getDetalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                DetalleOrdenCompraEntity detalle = new DetalleOrdenCompraEntity();
                detalle.setProducto(producto);
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                detalle.setSubtotal(detalleReq.getCantidad() * detalleReq.getPrecioUnitario());
                detalle.setOrdenCompra(ordenCompra);
                detalles.add(detalle);
                
                montoTotal += detalle.getSubtotal();
            }
        }
        
        ordenCompra.setDetalles(detalles);
        ordenCompra.setMontoTotal(montoTotal);
        
        OrdenCompraEntity saved = ordenCompraRepository.save(ordenCompra);
        return convertToResponse(saved);
    }
    
    /**
     * Crear orden de compra desde una cotización aprobada
     * @param cotizacionId ID de la cotización aprobada
     * @return OrdenCompraResponse
     */
    @Transactional
    public OrdenCompraResponse crearOrdenCompraDesdeCotizacion(Long cotizacionId) {
        log.info("Creando orden de compra desde cotización ID: {}", cotizacionId);
        
        // Buscar la cotización
        CotizacionEntity cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + cotizacionId));
        
        // Validar que la cotización esté aprobada
        if (!"APROBADO".equals(cotizacion.getEstado())) {
            throw new RuntimeException("Solo se pueden crear órdenes de compra desde cotizaciones aprobadas. Estado actual: " + cotizacion.getEstado());
        }
        
        // Validar que la cotización no esté expirada
        if (cotizacion.getFechaValidez() != null && cotizacion.getFechaValidez().isBefore(LocalDate.now())) {
            throw new RuntimeException("La cotización ha expirado. Fecha de validez: " + cotizacion.getFechaValidez());
        }
        
        // Crear la orden de compra
        OrdenCompraEntity ordenCompra = new OrdenCompraEntity();
        ordenCompra.setProveedor(cotizacion.getProveedor());
        ordenCompra.setDestino(cotizacion.getSolicitudPedido().getDestino());
        ordenCompra.setReferencia("COTIZACION: " + cotizacion.getNumeroCotizacion());
        ordenCompra.setFecha(LocalDate.now());
        ordenCompra.setEstado("CREADO");
        
        // Copiar los detalles de la cotización a la orden de compra
        List<DetalleOrdenCompraEntity> detalles = new ArrayList<>();
        double montoTotal = 0.0;
        
        for (DetalleCotizacionEntity detalleCot : cotizacion.getDetalles()) {
            DetalleOrdenCompraEntity detalle = new DetalleOrdenCompraEntity();
            detalle.setProducto(detalleCot.getProducto());
            detalle.setCantidad(detalleCot.getCantidad());
            detalle.setPrecioUnitario(detalleCot.getPrecioUnitario());
            detalle.setSubtotal(detalleCot.getSubtotal());
            detalle.setOrdenCompra(ordenCompra);
            detalles.add(detalle);
            
            montoTotal += detalle.getSubtotal();
        }
        
        ordenCompra.setDetalles(detalles);
        ordenCompra.setMontoTotal(montoTotal);
        
        OrdenCompraEntity saved = ordenCompraRepository.save(ordenCompra);
        
        log.info("Orden de compra creada desde cotización: {} -> {}", 
            cotizacion.getNumeroCotizacion(), saved.getNumeroOrden());
        
        return convertToResponse(saved);
    }
    
    @Transactional
    public OrdenCompraResponse enviarOrdenCompra(Long id) {
        log.info("Enviando orden de compra: {}", id);
        
        OrdenCompraEntity ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
        
        if (!"CREADO".equals(ordenCompra.getEstado())) {
            throw new RuntimeException("La orden de compra no se puede enviar porque su estado es: " + ordenCompra.getEstado());
        }
        
        ordenCompra.setEstado("ENVIADO");
        OrdenCompraEntity saved = ordenCompraRepository.save(ordenCompra);
        return convertToResponse(saved);
    }
    
    @Transactional
    public OrdenCompraResponse cancelarOrdenCompra(Long id) {
        log.info("Cancelando orden de compra: {}", id);
        
        OrdenCompraEntity ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
        
        if ("ENVIADO".equals(ordenCompra.getEstado())) {
            throw new RuntimeException("No se puede cancelar una orden de compra que ya fue enviada");
        }
        
        ordenCompra.setEstado("CANCELADO");
        OrdenCompraEntity saved = ordenCompraRepository.save(ordenCompra);
        return convertToResponse(saved);
    }
    
    public List<OrdenCompraResponse> listarOrdenesCompra() {
        return ordenCompraRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OrdenCompraResponse> listarPendientesEnvio() {
        return ordenCompraRepository.findOrdenesPendientesEnvio().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OrdenCompraResponse> listarPorProveedor(Long proveedorId) {
        return ordenCompraRepository.findByProveedorId(proveedorId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public OrdenCompraResponse obtenerOrdenCompra(Long id) {
        OrdenCompraEntity ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
        return convertToResponse(ordenCompra);
    }
    
    public Double obtenerTotalGastadoPorProveedor(Long proveedorId) {
        Double total = ordenCompraRepository.sumMontoTotalByProveedor(proveedorId);
        return total != null ? total : 0.0;
    }
    
    private OrdenCompraResponse convertToResponse(OrdenCompraEntity entity) {
        if (entity == null) return null;
        
        OrdenCompraResponse response = new OrdenCompraResponse();
        response.setId(entity.getId());
        response.setNumeroOrden(entity.getNumeroOrden());
        response.setProveedorId(entity.getProveedor().getId());
        response.setNombreProveedor(entity.getProveedor().getRazonSocial());
        response.setDestino(entity.getDestino());
        response.setReferencia(entity.getReferencia());
        response.setFecha(entity.getFecha());
        response.setMontoTotal(entity.getMontoTotal());
        response.setEstado(entity.getEstado());
        
        if (entity.getDetalles() != null) {
            List<DetalleOrdenCompraResponse> detalles = entity.getDetalles().stream()
                    .map(this::convertDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detalles);
        }
        
        return response;
    }
    
    private DetalleOrdenCompraResponse convertDetalleToResponse(DetalleOrdenCompraEntity entity) {
        DetalleOrdenCompraResponse response = new DetalleOrdenCompraResponse();
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
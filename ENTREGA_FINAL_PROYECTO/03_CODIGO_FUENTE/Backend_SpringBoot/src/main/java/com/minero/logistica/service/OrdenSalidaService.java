package com.minero.logistica.service;

import com.minero.logistica.entity.*;
import com.minero.logistica.model.request.OrdenSalidaRequest;
import com.minero.logistica.model.request.DetalleSalidaRequest;
import com.minero.logistica.model.response.OrdenSalidaResponse;
import com.minero.logistica.model.response.DetalleSalidaResponse;
import com.minero.logistica.repository.OrdenSalidaRepository;
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
public class OrdenSalidaService {

    private final OrdenSalidaRepository ordenSalidaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public OrdenSalidaResponse crearOrdenSalida(OrdenSalidaRequest request) {
        log.info("Creando orden de salida desde almacén: {}", request.getAlmacenOrigen());
        
        // Validar stock suficiente
        if (request.getDetalles() != null) {
            for (DetalleSalidaRequest detalleReq : request.getDetalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                if (producto.getStockActual() < detalleReq.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }
            }
        }
        
        OrdenSalidaEntity ordenSalida = new OrdenSalidaEntity();
        ordenSalida.setNombreOrden(request.getNombreOrden());
        ordenSalida.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        ordenSalida.setTrasladarA(request.getTrasladarA());
        ordenSalida.setOperadorAlmacen(request.getOperadorAlmacen());
        ordenSalida.setAlmacenOrigen(request.getAlmacenOrigen());
        ordenSalida.setGlosa(request.getGlosa());
        ordenSalida.setEstado("CREADO");
        
        List<DetalleSalidaEntity> detalles = new ArrayList<>();
        double total = 0.0;
        
        if (request.getDetalles() != null) {
            for (DetalleSalidaRequest detalleReq : request.getDetalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                DetalleSalidaEntity detalle = new DetalleSalidaEntity();
                detalle.setProducto(producto);
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecioReferencial());
                detalle.setSubtotal(detalleReq.getCantidad() * producto.getPrecioReferencial());
                detalle.setOrdenSalida(ordenSalida);
                detalles.add(detalle);
                
                total += detalle.getSubtotal();
            }
        }
        
        ordenSalida.setDetalles(detalles);
        ordenSalida.setTotal(total);
        
        OrdenSalidaEntity saved = ordenSalidaRepository.save(ordenSalida);
        return convertToResponse(saved);
    }
    
    @Transactional
    public OrdenSalidaResponse aprobarOrdenSalida(Long id) {
        log.info("Aprobando orden de salida: {}", id);
        
        OrdenSalidaEntity ordenSalida = ordenSalidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de salida no encontrada"));
        
        if (!"CREADO".equals(ordenSalida.getEstado())) {
            throw new RuntimeException("La orden de salida no se puede aprobar porque su estado es: " + ordenSalida.getEstado());
        }
        
        // Validar stock nuevamente y actualizar
        for (DetalleSalidaEntity detalle : ordenSalida.getDetalles()) {
            ProductoEntity producto = detalle.getProducto();
            if (producto.getStockActual() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }
            producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        ordenSalida.setEstado("APROBADO");
        OrdenSalidaEntity saved = ordenSalidaRepository.save(ordenSalida);
        return convertToResponse(saved);
    }
    
    @Transactional
    public OrdenSalidaResponse anularOrdenSalida(Long id) {
        log.info("Anulando orden de salida: {}", id);
        
        OrdenSalidaEntity ordenSalida = ordenSalidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de salida no encontrada"));
        
        ordenSalida.setEstado("ANULADO");
        OrdenSalidaEntity saved = ordenSalidaRepository.save(ordenSalida);
        return convertToResponse(saved);
    }
    
    public List<OrdenSalidaResponse> listarOrdenesSalida() {
        return ordenSalidaRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OrdenSalidaResponse> listarPendientesAprobacion() {
        return ordenSalidaRepository.findByEstado("CREADO").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OrdenSalidaResponse> listarPorAlmacenOrigen(String almacen) {
        return ordenSalidaRepository.findByAlmacenOrigen(almacen).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public OrdenSalidaResponse obtenerOrdenSalida(Long id) {
        OrdenSalidaEntity ordenSalida = ordenSalidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de salida no encontrada"));
        return convertToResponse(ordenSalida);
    }
    
    private OrdenSalidaResponse convertToResponse(OrdenSalidaEntity entity) {
        if (entity == null) return null;
        
        OrdenSalidaResponse response = new OrdenSalidaResponse();
        response.setId(entity.getId());
        response.setNumeroOS(entity.getNumeroOS());
        response.setNombreOrden(entity.getNombreOrden());
        response.setFecha(entity.getFecha());
        response.setTrasladarA(entity.getTrasladarA());
        response.setOperadorAlmacen(entity.getOperadorAlmacen());
        response.setAlmacenOrigen(entity.getAlmacenOrigen());
        response.setGlosa(entity.getGlosa());
        response.setTotal(entity.getTotal());
        response.setEstado(entity.getEstado());
        
        if (entity.getDetalles() != null) {
            List<DetalleSalidaResponse> detalles = entity.getDetalles().stream()
                    .map(this::convertDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detalles);
        }
        
        return response;
    }
    
    private DetalleSalidaResponse convertDetalleToResponse(DetalleSalidaEntity entity) {
        DetalleSalidaResponse response = new DetalleSalidaResponse();
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
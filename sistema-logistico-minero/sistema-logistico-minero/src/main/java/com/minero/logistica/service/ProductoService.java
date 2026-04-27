package com.minero.logistica.service;

import com.minero.logistica.entity.ProductoEntity;
import com.minero.logistica.model.request.ProductoRequest;
import com.minero.logistica.model.response.ApiResponse;
import com.minero.logistica.model.response.ProductoResponse;
import com.minero.logistica.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;
    
    /**
     * Registrar nuevo producto
     */
    @Transactional
    public ProductoResponse registrarProducto(ProductoRequest request) {
        log.info("Registrando nuevo producto: {}", request.getNombre());
        
        // Verificar si ya existe el código
        if (request.getCodigo() != null && productoRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe un producto con el código: " + request.getCodigo());
        }
        
        ProductoEntity producto = new ProductoEntity();
        producto.setCodigo(request.getCodigo() != null ? request.getCodigo() : generarCodigoProducto());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setPrecioReferencial(request.getPrecioReferencial());
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 0);
        producto.setStockActual(0);
        producto.setUbicacionFisica(request.getUbicacionFisica());
        producto.setActivo(true);
        
        ProductoEntity saved = productoRepository.save(producto);
        
        auditoriaService.registrar("CREAR_PRODUCTO", "PRODUCTO", 
            "Producto creado: " + saved.getNombre() + " - Código: " + saved.getCodigo());
        
        return convertToResponse(saved);
    }
    
    /**
     * Listar todos los productos
     */
    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar productos activos
     */
    public List<ProductoResponse> listarProductosActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar productos por categoría
     */
    public List<ProductoResponse> listarProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar productos con stock crítico (RF_14 - Control de Stocks)
     */
    public List<ProductoResponse> listarProductosStockCritico() {
        return productoRepository.findProductosConStockCritico().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar productos sin stock
     */
    public List<ProductoResponse> listarProductosSinStock() {
        return productoRepository.findProductosSinStock().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar productos por nombre o descripción (RNF_04 - Autocompletar)
     */
    public List<ProductoResponse> buscarProductos(String termino) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(termino, termino)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener producto por ID
     */
    public ProductoResponse obtenerProducto(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertToResponse(producto);
    }
    
    /**
     * Obtener producto por código
     */
    public ProductoResponse obtenerProductoPorCodigo(String codigo) {
        ProductoEntity producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));
        return convertToResponse(producto);
    }
    
    /**
     * Actualizar producto
     */
    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (request.getNombre() != null) producto.setNombre(request.getNombre());
        if (request.getDescripcion() != null) producto.setDescripcion(request.getDescripcion());
        if (request.getCategoria() != null) producto.setCategoria(request.getCategoria());
        if (request.getUnidadMedida() != null) producto.setUnidadMedida(request.getUnidadMedida());
        if (request.getPrecioReferencial() != null) producto.setPrecioReferencial(request.getPrecioReferencial());
        if (request.getStockMinimo() != null) producto.setStockMinimo(request.getStockMinimo());
        if (request.getUbicacionFisica() != null) producto.setUbicacionFisica(request.getUbicacionFisica());
        
        ProductoEntity updated = productoRepository.save(producto);
        
        auditoriaService.registrar("ACTUALIZAR_PRODUCTO", "PRODUCTO", 
            "Producto actualizado: " + updated.getNombre());
        
        return convertToResponse(updated);
    }
    
    /**
     * Actualizar stock de producto (RF_14 - Control de Stocks)
     */
    @Transactional
    public ApiResponse<?> actualizarStock(Long productoId, Integer cantidad) {
        productoRepository.actualizarStock(productoId, cantidad);
        
        ProductoEntity producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            auditoriaService.registrar("ACTUALIZAR_STOCK", "PRODUCTO", 
                "Stock actualizado para: " + producto.getNombre() + 
                " - Cambio: " + cantidad + " - Nuevo stock: " + (producto.getStockActual() + cantidad));
        }
        
        return ApiResponse.success(null, "Stock actualizado correctamente");
    }
    
    /**
     * Cambiar estado del producto (Activo/Inactivo)
     */
    @Transactional
    public ApiResponse<?> cambiarEstadoProducto(Long id, Boolean activo) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setActivo(activo);
        productoRepository.save(producto);
        
        auditoriaService.registrar("CAMBIAR_ESTADO_PRODUCTO", "PRODUCTO", 
            "Producto " + producto.getNombre() + " estado: " + (activo ? "ACTIVO" : "INACTIVO"));
        
        return ApiResponse.success(null, "Estado del producto actualizado");
    }
    
    /**
     * Eliminar producto
     */
    @Transactional
    public ApiResponse<?> eliminarProducto(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        productoRepository.delete(producto);
        
        auditoriaService.registrar("ELIMINAR_PRODUCTO", "PRODUCTO", 
            "Producto eliminado: " + producto.getNombre());
        
        return ApiResponse.success(null, "Producto eliminado correctamente");
    }
    
    /**
     * Generar código único para producto
     */
    private String generarCodigoProducto() {
        long count = productoRepository.count() + 1;
        return String.format("PROD-%05d", count);
    }
    
    private ProductoResponse convertToResponse(ProductoEntity entity) {
        ProductoResponse response = new ProductoResponse();
        response.setId(entity.getId());
        response.setCodigo(entity.getCodigo());
        response.setNombre(entity.getNombre());
        response.setDescripcion(entity.getDescripcion());
        response.setCategoria(entity.getCategoria());
        response.setUnidadMedida(entity.getUnidadMedida());
        response.setPrecioReferencial(entity.getPrecioReferencial());
        response.setStockMinimo(entity.getStockMinimo());
        response.setStockActual(entity.getStockActual());
        response.setUbicacionFisica(entity.getUbicacionFisica());
        response.setImagenUrl(entity.getImagenUrl());
        return response;
    }
}
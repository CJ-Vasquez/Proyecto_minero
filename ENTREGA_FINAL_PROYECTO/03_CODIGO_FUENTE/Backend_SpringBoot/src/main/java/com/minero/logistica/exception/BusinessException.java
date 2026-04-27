package com.minero.logistica.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final Object[] args;
    
    // Códigos de error predefinidos
    public static final String STOCK_INSUFICIENTE = "STOCK_001";
    public static final String PRODUCTO_NO_ENCONTRADO = "PROD_001";
    public static final String PROVEEDOR_NO_ENCONTRADO = "PROV_001";
    public static final String USUARIO_BLOQUEADO = "USER_001";
    public static final String PASSWORD_INCORRECTA = "AUTH_001";
    public static final String SOLICITUD_NO_APROBABLE = "SOL_001";
    public static final String COTIZACION_EXPIRADA = "COT_001";
    public static final String ORDEN_COMPRA_NO_ENVIABLE = "OC_001";
    public static final String STOCK_MINIMO_NO_CUMPLE = "STOCK_002";
    public static final String RUC_DUPLICADO = "PROV_002";
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.args = null;
    }
    
    public BusinessException(String code, String message, Object[] args) {
        super(message);
        this.code = code;
        this.args = args;
    }
    
    // Métodos estáticos para crear excepciones comunes
    
    public static BusinessException stockInsuficiente(String productoNombre, Integer stockActual, Integer requerido) {
        return new BusinessException(STOCK_INSUFICIENTE, 
            String.format("Stock insuficiente para '%s'. Stock actual: %d, Requerido: %d", 
                productoNombre, stockActual, requerido));
    }
    
    public static BusinessException productoNoEncontrado(Long id) {
        return new BusinessException(PRODUCTO_NO_ENCONTRADO, 
            String.format("Producto con ID %d no encontrado", id));
    }
    
    public static BusinessException proveedorNoEncontrado(Long id) {
        return new BusinessException(PROVEEDOR_NO_ENCONTRADO, 
            String.format("Proveedor con ID %d no encontrado", id));
    }
    
    public static BusinessException usuarioBloqueado(String username) {
        return new BusinessException(USUARIO_BLOQUEADO, 
            String.format("Usuario '%s' bloqueado. Contacte al administrador", username));
    }
    
    public static BusinessException passwordIncorrecta() {
        return new BusinessException(PASSWORD_INCORRECTA, 
            "Contraseña actual incorrecta");
    }
    
    public static BusinessException solicitudNoAprobable(String numeroPedido, String estadoActual) {
        return new BusinessException(SOLICITUD_NO_APROBABLE, 
            String.format("La solicitud %s no puede ser aprobada. Estado actual: %s", 
                numeroPedido, estadoActual));
    }
    
    public static BusinessException cotizacionExpirada(String numeroCotizacion) {
        return new BusinessException(COTIZACION_EXPIRADA, 
            String.format("La cotización %s ha expirado", numeroCotizacion));
    }
    
    public static BusinessException ordenCompraNoEnviable(String numeroOrden) {
        return new BusinessException(ORDEN_COMPRA_NO_ENVIABLE, 
            String.format("La orden de compra %s no puede ser enviada. Estado actual no es CREADO", numeroOrden));
    }
    
    public static BusinessException rucDuplicado(String ruc) {
        return new BusinessException(RUC_DUPLICADO, 
            String.format("Ya existe un proveedor con RUC: %s", ruc));
    }
}
# Base de Datos - Sistema Logistico Minero

## Esquema: `minero_logistica` (MySQL 8.0)

### Opcion A: Creacion automatica (recomendado)

Spring Boot crea y actualiza las tablas automaticamente al arrancar gracias a:

```properties
spring.jpa.hibernate.ddl-auto=update
```

**Pasos:**
1. Instalar MySQL 8.0
2. Ejecutar `01_crear_base_datos.sql` para crear el esquema vacio
3. Arrancar el backend: `./mvnw spring-boot:run`
4. Spring Boot crea las 11 tablas + FKs automaticamente
5. `DataInitializer` crea los 5 usuarios por defecto con BCrypt

### Opcion B: Datos de ejemplo

Despues del primer arranque, opcionalmente ejecutar:
- `02_datos_iniciales.sql` para cargar 8 productos y 5 proveedores de ejemplo

### Usuarios por defecto (creados por DataInitializer)

Todos los passwords estan cifrados con BCryptPasswordEncoder:

| Username | Password (texto plano) | Rol |
|---|---|---|
| admin | admin123 | ADMIN |
| gerente | gerente123 | GERENTE |
| jefe | jefe123 | JEFE_ALMACEN |
| compras | compras123 | ASISTENTE_COMPRAS |
| almacen | almacen123 | ASISTENTE_ALMACEN |

### Tablas principales

1. usuarios
2. productos
3. proveedores
4. solicitudes_pedido + detalles_pedido
5. cotizaciones + detalles_cotizacion
6. ordenes_compra + detalles_orden_compra
7. recepciones + detalles_recepcion
8. ordenes_salida + detalles_salida
9. kardex
10. auditoria

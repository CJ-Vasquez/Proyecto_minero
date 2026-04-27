# Sistema Logistico Minero - Tauro S.A.C.

Proyecto del curso Desarrollo de Aplicaciones Web I (4694) - CIBERTEC.

Aplicacion web para gestionar el proceso logistico de una empresa minera: compras, almacen, recepciones, kardex y ordenes de salida.

## Integrantes

- Nicolas Yepez Rodriguez
- Ciro Vasquez Malpartida
- Luis Hider Manco Berrocal
- Gretta Cristina Pareja Alcazaba

## Tecnologias

Backend:
- Java 17
- Spring Boot 3.1.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 8

Frontend:
- Angular 21
- Bootstrap 5
- Chart.js
- SweetAlert2

## Como ejecutar el proyecto

### 1. Base de datos

Tener MySQL corriendo en localhost:3306. Crear la base de datos:

```sql
CREATE DATABASE minero_logistica
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Luego cargar los datos iniciales (productos y proveedores):

```
mysql -u root -p minero_logistica < ENTREGA_FINAL_PROYECTO/07_BASE_DE_DATOS/02_datos_iniciales.sql
```

### 2. Backend

Editar `sistema-logistico-minero/sistema-logistico-minero/src/main/resources/application.properties` y poner el password de tu MySQL.

```
cd sistema-logistico-minero/sistema-logistico-minero
mvnw clean package -DskipTests
java -jar target/logistica-minero-1.0.0.jar
```

El backend queda en http://localhost:8082

### 3. Frontend

```
cd sistema-logistico_V3/sistema-logistico
npm install
npm start
```

El frontend queda en http://localhost:4200

## Usuarios de prueba

Los usuarios se crean automaticamente al primer arranque del backend (DataInitializer.java).

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin123 | ADMIN |
| gerente | gerente123 | GERENTE |
| jefe | jefe123 | JEFE_ALMACEN |
| compras | compras123 | ASISTENTE_COMPRAS |
| almacen | almacen123 | ASISTENTE_ALMACEN |

## Modulos

- Dashboard general
- Panel Gerencial (con KPIs y graficos)
- Gestion Gerencial (centro de aprobaciones)
- Productos
- Proveedores
- Solicitudes de pedido
- Cotizaciones
- Ordenes de compra
- Recepciones
- Ordenes de salida
- Kardex
- Usuarios
- Reportes

## Documentacion API

Con el backend corriendo: http://localhost:8082/swagger-ui/index.html

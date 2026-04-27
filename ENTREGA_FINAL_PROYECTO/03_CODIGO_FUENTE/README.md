# 03 - CODIGO FUENTE

## Estructura

```
03_CODIGO_FUENTE/
|-- Backend_SpringBoot/       Backend Java con Spring Boot 3.1.5
`-- Frontend_Angular/         Frontend TypeScript con Angular 21
```

## Backend (Spring Boot)

**Ubicacion:** `Backend_SpringBoot/`

**Tecnologias principales:**
- Java 17
- Spring Boot 3.1.5
- Spring Data JPA + Hibernate
- Spring Security + JWT + BCryptPasswordEncoder
- MySQL 8.0 (driver)
- H2 in-memory (solo para tests)
- Lombok
- SpringDoc OpenAPI (Swagger)

**Comandos:**
```bash
cd Backend_SpringBoot/

# Compilar
./mvnw clean install

# Ejecutar (puerto 8082)
./mvnw spring-boot:run

# Ejecutar tests (22 tests)
./mvnw test
```

**Endpoints:**
- API REST: http://localhost:8082/api/
- Swagger UI: http://localhost:8082/swagger-ui.html

**Archivos importantes:**
- `pom.xml` - Dependencias Maven
- `src/main/resources/application.properties` - Configuracion
- `src/main/java/com/minero/logistica/config/SecurityConfig.java` - BCryptPasswordEncoder
- `src/main/java/com/minero/logistica/config/DataInitializer.java` - Migracion BCrypt + usuarios por defecto
- `src/main/java/com/minero/logistica/service/AuthService.java` - Login con passwordEncoder.matches()

**NOTA:** El directorio `target/` ha sido excluido. Se generara al compilar.

---

## Frontend (Angular)

**Ubicacion:** `Frontend_Angular/`

**Tecnologias principales:**
- Angular 21 (standalone components, lazy loading)
- TypeScript 5.9
- Bootstrap 5.3
- Chart.js + ng2-charts
- SweetAlert2
- jsPDF + jspdf-autotable
- SheetJS (xlsx)
- ngx-pagination

**Comandos:**
```bash
cd Frontend_Angular/

# Instalar dependencias
npm install

# Ejecutar (puerto 4200)
npm start
```

**URL:** http://localhost:4200

**Archivos importantes:**
- `package.json` - Dependencias npm
- `angular.json` - Configuracion de Angular CLI
- `src/app/app.routes.ts` - Rutas con guards
- `src/app/services/auth.service.ts` - Autenticacion
- `src/app/guards/auth.guard.ts` - Guard de autenticacion
- `src/app/interceptors/auth.interceptor.ts` - Interceptor JWT

**NOTA:** Los directorios `node_modules/`, `.angular/` y `dist/` han sido excluidos. Se generaran al ejecutar `npm install` y `npm start`.

---

## Requisitos previos

| Herramienta | Version | Proposito |
|---|---|---|
| Java JDK | 17+ | Backend |
| Maven | 3.9+ (incluido mvnw) | Build backend |
| Node.js | 20+ | Frontend |
| npm | 11+ | Gestor de paquetes |
| MySQL | 8.0 | Base de datos |

## Configuracion de la base de datos

Antes de arrancar el backend, editar `Backend_SpringBoot/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/minero_logistica
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
```

Y ejecutar el script SQL de `../07_BASE_DE_DATOS/01_crear_base_datos.sql`.

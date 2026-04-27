# ENTREGA FINAL - Sistema Logistico Minero

**Curso:** Desarrollo de Aplicaciones Web I (4694)
**Escuela:** Tecnologia
**Carrera:** Computacion e Informatica
**Ciclo:** Quinto
**Institucion:** CIBERTEC

---

## Estructura de la Entrega

```
ENTREGA_FINAL_PROYECTO/
|
|-- 01_INFORME/                      [PRINCIPAL]
|   |-- INFORME_PROYECTO_SISTEMA_LOGISTICO_MINERO.docx
|   `-- Analisis_Tecnico_Preliminar.docx
|
|-- 02_DIAGRAMAS_UML/                [6 diagramas]
|   |-- 01_arquitectura.png / .puml
|   |-- 02_casos_de_uso.png / .puml
|   |-- 03_diagrama_clases.png / .puml
|   |-- 04_diagrama_secuencia.png / .puml
|   |-- 05_diagrama_er.png / .puml
|   `-- 06_diagrama_despliegue.png / .puml
|
|-- 03_CODIGO_FUENTE/                [Backend + Frontend]
|   |-- Backend_SpringBoot/          Java 17 + Spring Boot 3.1.5
|   `-- Frontend_Angular/            Angular 21 + Bootstrap 5
|
|-- 04_DOCUMENTACION_CURSO/
|   `-- Plan_Proyecto_Curso_4694.pdf
|
|-- 05_PRESENTACION_PPT/             [Por completar]
|   `-- README.md (guion sugerido)
|
|-- 06_VIDEO_DEMO_REEL/              [Por completar]
|   `-- README.md (guion sugerido)
|
|-- 07_BASE_DE_DATOS/
|   |-- 01_crear_base_datos.sql
|   |-- 02_datos_iniciales.sql
|   `-- README.md
|
`-- README.md                         (este archivo)
```

---

## Que incluye esta entrega

### [v] Presentables completados

| # | Presentable | Carpeta | Estado |
|---|---|---|---|
| 1 | Informe principal del proyecto (DOCX) | 01_INFORME/ | **COMPLETO** |
| 2 | Analisis tecnico preliminar (DOCX) | 01_INFORME/ | **COMPLETO** |
| 3 | 6 diagramas UML (PNG + PlantUML) | 02_DIAGRAMAS_UML/ | **COMPLETO** |
| 4 | Codigo fuente backend Spring Boot | 03_CODIGO_FUENTE/Backend_SpringBoot/ | **COMPLETO** |
| 5 | Codigo fuente frontend Angular | 03_CODIGO_FUENTE/Frontend_Angular/ | **COMPLETO** |
| 6 | Plan de proyecto del curso | 04_DOCUMENTACION_CURSO/ | **COMPLETO** |
| 7 | Scripts SQL de base de datos | 07_BASE_DE_DATOS/ | **COMPLETO** |

### [ ] Presentables por completar

| # | Presentable | Carpeta | Pendiente |
|---|---|---|---|
| 8 | Presentacion PowerPoint (.pptx) | 05_PRESENTACION_PPT/ | El guion sugerido esta en el README |
| 9 | Video Demo Reel (3-5 minutos) | 06_VIDEO_DEMO_REEL/ | El guion sugerido esta en el README |

---

## Requisitos cumplidos del curso

Segun el Plan de Proyecto (Seccion 4: Especificacion y Alcance):

- [v] **Servicio Web REST de Login** con usuario y password en Base de Datos
- [v] **Password cifrado con BCryptPasswordEncoder** en la Base de Datos
- [v] **Aplicacion web que consume servicios REST** con metodos GET, POST, PUT y DELETE
- [v] **Frontend en Angular** (versión 21)
- [v] **Persistencia en Base de Datos** (MySQL 8.0)
- [v] **Pruebas de la capa de acceso a datos** (22 tests con JUnit 5 + H2)

### Rubrica de evaluacion

**AP1 (Avance 50% - Sesion 11):**
- Servicio Web REST Login: **6/6** (con BCrypt)
- Pruebas CRUD capa datos: **6/6** (22 tests, 100% pass)
- Total tecnico AP1: **12/12**

**SP1 (Final 100% - Sesion 13):**
- Servicios REST (GET, POST, PUT, DELETE): **6/6**
- Angular consume servicios REST: **6/6**
- Total tecnico SP1: **12/12**

---

## Instalacion rapida

### Requisitos previos

- **Java 17** (OpenJDK o Oracle)
- **Maven 3.9+** (incluido en el proyecto mvnw)
- **Node.js 20+** y **npm 11+**
- **MySQL 8.0** corriendo en localhost:3306

### 1. Base de datos

```bash
mysql -u root -p < 07_BASE_DE_DATOS/01_crear_base_datos.sql
```

### 2. Backend (puerto 8082)

```bash
cd 03_CODIGO_FUENTE/Backend_SpringBoot/
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8082/swagger-ui.html

### 3. Frontend (puerto 4200)

```bash
cd 03_CODIGO_FUENTE/Frontend_Angular/
npm install
npm start
```

Aplicacion: http://localhost:4200

### 4. Usuarios por defecto

Creados automaticamente por DataInitializer con BCrypt:

| Username | Password | Rol |
|---|---|---|
| admin | admin123 | ADMIN |
| gerente | gerente123 | GERENTE |
| jefe | jefe123 | JEFE_ALMACEN |
| compras | compras123 | ASISTENTE_COMPRAS |
| almacen | almacen123 | ASISTENTE_ALMACEN |

---

## Ejecutar pruebas

```bash
cd 03_CODIGO_FUENTE/Backend_SpringBoot/
./mvnw test
```

**Resultado esperado:**
```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Integrantes del Grupo

(Completar antes de la entrega)

| Rol | Nombre | Codigo |
|---|---|---|
| Coordinador | [Integrante 1] | [I2024xxxxx] |
| Integrante | [Integrante 2] | [I2024xxxxx] |
| Integrante | [Integrante 3] | [I2024xxxxx] |
| Integrante | [Integrante 4] | [I2024xxxxx] |
| Integrante | [Integrante 5] | [I2024xxxxx] |

**Docente:** [Nombre del Docente]

---

## Resumen tecnico del proyecto

- **11 entidades** JPA con relaciones
- **89 endpoints REST** (GET, POST, PUT, PATCH, DELETE)
- **12 paginas** Angular con componentes standalone
- **5 roles** con control de acceso granular (RBAC)
- **22 tests** automatizados (100% pass rate)
- **25 casos de prueba** funcionales
- **6 diagramas UML** profesionales

**Lima, Abril 2026**

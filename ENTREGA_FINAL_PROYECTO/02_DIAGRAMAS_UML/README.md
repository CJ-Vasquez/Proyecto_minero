# 02 - DIAGRAMAS UML

## 6 Diagramas del Sistema Logistico Minero

Cada diagrama incluye dos archivos:
- `.png` - imagen renderizada lista para usar en documentos
- `.puml` - codigo fuente PlantUML (editable)

| # | Archivo | Descripcion |
|---|---|---|
| 1 | `01_arquitectura.png` | Arquitectura Spring Boot + Angular en 7 capas |
| 2 | `02_casos_de_uso.png` | 5 actores + 12 casos de uso |
| 3 | `03_diagrama_clases.png` | Services, Repositories, Entities y sus relaciones |
| 4 | `04_diagrama_secuencia.png` | Flujo de Login REST con JWT + BCrypt |
| 5 | `05_diagrama_er.png` | Modelo E-R con 10 tablas del esquema minero_logistica |
| 6 | `06_diagrama_despliegue.png` | Despliegue (Cliente, Servidor, BD) |

## Como editar los diagramas

Los archivos `.puml` son texto plano y se pueden editar con cualquier editor.

### Opcion 1: Online (sin instalar nada)
1. Copiar el contenido del .puml
2. Pegarlo en https://www.plantuml.com/plantuml/
3. Descargar el PNG resultante

### Opcion 2: VS Code
1. Instalar la extension "PlantUML" de jebbs
2. Abrir el archivo .puml
3. Alt + D para preview

### Opcion 3: Linea de comandos
```bash
# Requiere Java y plantuml.jar
java -jar plantuml.jar 01_arquitectura.puml
```

## Uso en el informe

Los 6 diagramas ya estan embebidos en el informe principal:
- Diagramas 1-5: en las secciones 8.1 a 8.5 (Producto)
- Los 6 diagramas: en el Anexo H (compendio)

# Video Demo Reel - Sistema Logistico Minero

## Por completar

Crear el archivo `DemoReel_SistemaLogisticoMinero.mp4` con las siguientes caracteristicas:

### Especificaciones tecnicas

- **Duracion:** 3-5 minutos (requisito del curso)
- **Resolucion:** 1080p (1920x1080)
- **Formato:** MP4 (H.264)
- **Audio:** narracion clara en espanol

### Guion sugerido

#### Intro (0:00 - 0:20)
- Titulo: "Sistema Logistico Minero"
- Breve presentacion del equipo
- Curso: Desarrollo de Aplicaciones Web I

#### Contexto del problema (0:20 - 0:45)
- Sector minero peruano
- Problematica: rotura de stock, registros manuales, perdidas operativas

#### Arquitectura (0:45 - 1:15)
- Diagrama de arquitectura Spring Boot + Angular
- Mencion del stack: Spring Boot 3.1.5, Angular 21, MySQL, JWT + BCrypt

#### Demo en vivo (1:15 - 4:00)

**1. Login con BCrypt (1:15 - 1:35)**
- Mostrar pantalla de login
- Ingresar credenciales: admin / admin123
- Evidenciar que el password esta cifrado en BD (mostrar Workbench con hash $2a$...)

**2. Dashboard (1:35 - 2:00)**
- KPIs en tiempo real
- Graficos con Chart.js
- Menu segun rol

**3. Flujo logistico completo (2:00 - 3:30)**
- Crear solicitud de pedido
- Enviar a aprobacion
- Login como Gerente -> Aprobar
- Login como Asistente Compras -> Generar cotizacion
- Aprobar cotizacion
- Crear orden de compra
- Enviar OC al proveedor
- Login como Jefe Almacen -> Registrar recepcion
- Ver kardex FIFO actualizado

**4. Reportes (3:30 - 3:50)**
- Exportar reporte PDF
- Exportar reporte Excel

#### Pruebas automatizadas (3:50 - 4:20)
- Ejecutar `./mvnw test` en terminal
- Mostrar "Tests run: 22, Failures: 0, Errors: 0"
- BUILD SUCCESS

#### Cierre (4:20 - 5:00)
- Resumen: 89 endpoints REST, 47 tests exitosos, 11 modulos CRUD
- Mencion de BCryptPasswordEncoder como requisito del curso cumplido
- Agradecimientos

### Herramientas recomendadas

- **Grabacion de pantalla:** OBS Studio (gratis), Loom, Camtasia
- **Edicion:** DaVinci Resolve (gratis), Shotcut, Adobe Premiere
- **Grabacion de audio:** micrófono USB o headset, Audacity para limpiar audio

### Tips

- Grabar con audio limpio (sin ruido de fondo)
- Usar cursor visible y zoom en secciones importantes
- Incluir subtitulos (opcional pero recomendado)
- Mostrar ambos navegadores (frontend en 4200) y Swagger UI (8082)

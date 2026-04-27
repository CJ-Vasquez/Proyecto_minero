-- =====================================================
-- SISTEMA LOGÍSTICO MINERO - DATOS DE PRUEBA
-- Empresa: Minera Tauro S.A.C.
-- =====================================================

-- =====================================================
-- 1. LIMPIEZA DE TABLAS (en orden correcto por FK)
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE detalle_salida;
TRUNCATE TABLE ordenes_salida;
TRUNCATE TABLE detalle_recepcion;
TRUNCATE TABLE recepciones;
TRUNCATE TABLE detalle_orden_compra;
TRUNCATE TABLE ordenes_compra;
TRUNCATE TABLE detalle_cotizacion;
TRUNCATE TABLE cotizaciones;
TRUNCATE TABLE detalle_pedido;
TRUNCATE TABLE solicitudes_pedido;
TRUNCATE TABLE kardex;
TRUNCATE TABLE productos;
TRUNCATE TABLE proveedores;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE auditoria;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 2. USUARIOS (RFG_02 - Administrar Usuarios)
-- Passwords encriptados con BCrypt: "password123"
-- =====================================================

INSERT INTO usuarios (id, username, password, email, nombres, apellidos, cargo, rol, activo, bloqueado, intentos_fallidos, fecha_creacion, ultimo_cambio_password) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'admin@mineratauro.com', 'Administrador', 'Sistema', 'Administrador TI', 'ADMIN', true, false, 0, NOW(), NOW()),
(2, 'gerente', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'gerente@mineratauro.com', 'Carlos', 'Mendoza', 'Gerente General', 'GERENTE', true, false, 0, NOW(), NOW()),
(3, 'jefe_almacen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'jefe.almacen@mineratauro.com', 'Roberto', 'Quispe', 'Jefe de Almacén', 'JEFE_ALMACEN', true, false, 0, NOW(), NOW()),
(4, 'asistente_compras', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'compras@mineratauro.com', 'Maria', 'Lopez', 'Asistente de Compras', 'ASISTENTE_COMPRAS', true, false, 0, NOW(), NOW()),
(5, 'asistente_almacen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'almacen@mineratauro.com', 'Juan', 'Perez', 'Asistente de Almacén', 'ASISTENTE_ALMACEN', true, false, 0, NOW(), NOW()),
(6, 'usuario_mina', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'mina@mineratauro.com', 'Pedro', 'Gonzales', 'Jefe de Mina', 'ASISTENTE_ALMACEN', true, false, 0, NOW(), NOW());

-- =====================================================
-- 3. PROVEEDORES (CUS09 - Gestionar Proveedores)
-- =====================================================

INSERT INTO proveedores (id, codigo, razon_social, ruc, nombre_contacto, telefono, email, direccion, estado, prioridad, puntaje_evaluacion, fecha_registro) VALUES
(1, 'PROV-00001', 'Ferreyros S.A.', '20100123456', 'Juan Castro', '01-555-1001', 'ventas@ferreyros.com.pe', 'Av. Argentina 1234, Callao', 'ACTIVO', 1, 45.5, NOW()),
(2, 'PROV-00002', 'Komatsu Mitsui Maquinarias', '20100234567', 'Maria Torres', '01-555-1002', 'ventas@komatsu.com.pe', 'Av. Colonial 567, Lima', 'ACTIVO', 1, 44.0, NOW()),
(3, 'PROV-00003', 'Graña y Montero', '20100345678', 'Carlos Rivas', '01-555-1003', 'ventas@gym.com.pe', 'Av. Paseo de la República 456, Lima', 'ACTIVO', 2, 38.5, NOW()),
(4, 'PROV-00004', 'Seguridad Minera EIRL', '20100456789', 'Ana Quispe', '01-555-1004', 'ventas@segmin.com.pe', 'Av. Industrial 789, Arequipa', 'ACTIVO', 2, 37.0, NOW()),
(5, 'PROV-00005', 'Repuestos Industriales SAC', '20100567890', 'Luis Fernández', '01-555-1005', 'ventas@repuestos.com.pe', 'Av. Los Pinos 321, Trujillo', 'ACTIVO', 3, 32.5, NOW()),
(6, 'PROV-00006', '3M Perú', '20100678901', 'Patricia Rojas', '01-555-1006', 'ventas@3m.com.pe', 'Av. Javier Prado 890, Lima', 'ACTIVO', 1, 42.0, NOW()),
(7, 'PROV-00007', 'Herramientas Andinas', '20100789012', 'Fernando Díaz', '01-555-1007', 'ventas@handinas.com', 'Calle Los Alamos 234, Arequipa', 'INACTIVO', 3, 31.0, NOW()),
(8, 'PROV-00008', 'Lubricantes Vistony', '20100890123', 'Ricardo Paz', '01-555-1008', 'ventas@vistony.com.pe', 'Av. Universitaria 567, Callao', 'ACTIVO', 2, 36.5, NOW());

-- =====================================================
-- 4. PRODUCTOS (Catálogo)
-- =====================================================

INSERT INTO productos (id, codigo, nombre, descripcion, categoria, unidad_medida, precio_referencial, stock_minimo, stock_actual, ubicacion_fisica, activo) VALUES
-- EPP (Equipos de Protección Personal)
(1, 'EPP-001', 'Casco de Seguridad', 'Casco industrial con suspensión, color blanco', 'EPP', 'UNIDAD', 45.00, 50, 120, 'A-01-001', true),
(2, 'EPP-002', 'Lentes de Seguridad', 'Lentes anti-impacto con protección UV', 'EPP', 'UNIDAD', 25.00, 100, 250, 'A-01-002', true),
(3, 'EPP-003', 'Guantes de Cuero', 'Guantes de cuero vacuno para minería', 'EPP', 'PAR', 35.00, 80, 180, 'A-01-003', true),
(4, 'EPP-004', 'Botas de Seguridad', 'Botas dieléctricas con punta de acero', 'EPP', 'PAR', 120.00, 40, 95, 'A-01-004', true),
(5, 'EPP-005', 'Arnés de Seguridad', 'Arnés de cuerpo completo para trabajo en altura', 'EPP', 'UNIDAD', 180.00, 20, 45, 'A-01-005', true),
(6, 'EPP-006', 'Respirador', 'Respirador de doble filtro para polvo', 'EPP', 'UNIDAD', 65.00, 60, 110, 'A-01-006', true),

-- Repuestos
(7, 'REP-001', 'Filtro de Aire', 'Filtro de aire para perforadora', 'REPUESTOS', 'UNIDAD', 85.00, 30, 25, 'B-01-001', true),
(8, 'REP-002', 'Bujía', 'Bujía para motor diésel', 'REPUESTOS', 'UNIDAD', 15.00, 100, 80, 'B-01-002', true),
(9, 'REP-003', 'Correa de Transmisión', 'Correa para equipo de perforación', 'REPUESTOS', 'UNIDAD', 55.00, 25, 15, 'B-01-003', true),
(10, 'REP-004', 'Rodamiento', 'Rodamiento para maquinaria pesada', 'REPUESTOS', 'UNIDAD', 95.00, 20, 12, 'B-01-004', true),

-- Herramientas
(11, 'HER-001', 'Taladro Percutor', 'Taladro percutor 1/2" industrial', 'HERRAMIENTAS', 'UNIDAD', 350.00, 10, 8, 'C-01-001', true),
(12, 'HER-002', 'Juego de Llaves', 'Juego de llaves mixtas 8-24mm', 'HERRAMIENTAS', 'JUEGO', 120.00, 15, 12, 'C-01-002', true),
(13, 'HER-003', 'Martillo', 'Martillo de bola 2.5 lbs', 'HERRAMIENTAS', 'UNIDAD', 45.00, 20, 18, 'C-01-003', true),

-- Lubricantes
(14, 'LUB-001', 'Aceite Motor 15W40', 'Aceite para motor diésel (galón)', 'LUBRICANTES', 'GALON', 45.00, 50, 35, 'D-01-001', true),
(15, 'LUB-002', 'Grasa Lithium', 'Grasa para rodamientos', 'LUBRICANTES', 'KILO', 12.00, 100, 75, 'D-01-002', true),

-- Materiales de perforación
(16, 'PER-001', 'Broca de Perforación', 'Broca de tungsteno 2"', 'PERFORACION', 'UNIDAD', 250.00, 30, 18, 'E-01-001', true),
(17, 'PER-002', 'Barra de Perforación', 'Barra de acero 6 pies', 'PERFORACION', 'UNIDAD', 180.00, 25, 15, 'E-01-002', true),
(18, 'PER-003', 'Adaptador de Perforación', 'Adaptador para broca', 'PERFORACION', 'UNIDAD', 75.00, 30, 22, 'E-01-003', true);

-- =====================================================
-- 5. SOLICITUDES DE PEDIDO (CUS01, CUS02, CUS03)
-- =====================================================

INSERT INTO solicitudes_pedido (id, numero_pedido, origen, solicitante, oficina, glosa, destino, aprobador, almacen, fecha, estado, motivo_rechazo) VALUES
(1, 'PED-202501001', 'MINA', 'Pedro Gonzales', 'Oficina Mina', 'Solicitud de EPP para nuevo personal', 'LIMA', 'Carlos Mendoza', 'LIMA', '2025-01-05', 'APROBADO', NULL),
(2, 'PED-202501002', 'MINA', 'Juan Perez', 'Oficina Mina', 'Repuestos urgentes para perforadora', 'LIMA', 'Carlos Mendoza', 'LIMA', '2025-01-10', 'APROBADO', NULL),
(3, 'PED-202501003', 'LIMA', 'Roberto Quispe', 'Oficina Lima', 'Stock crítico de lubricantes', 'LIMA', 'Carlos Mendoza', 'LIMA', '2025-01-15', 'PENDIENTE_APROBACION', NULL),
(4, 'PED-202501004', 'MINA', 'Pedro Gonzales', 'Oficina Mina', 'Herramientas para taller', 'TRUJILLO', 'Carlos Mendoza', 'TRUJILLO', '2025-01-20', 'APROBADO', NULL),
(5, 'PED-202501005', 'LIMA', 'Roberto Quispe', 'Oficina Lima', 'Reposición de stock EPP', 'LIMA', NULL, 'LIMA', '2025-01-25', 'RECHAZADO', 'Stock suficiente en almacén'),
(6, 'PED-202502001', 'MINA', 'Pedro Gonzales', 'Oficina Mina', 'Materiales de perforación', 'LIMA', 'Carlos Mendoza', 'LIMA', '2025-02-01', 'PENDIENTE_APROBACION', NULL),
(7, 'PED-202502002', 'TRUJILLO', 'Maria Lopez', 'Oficina Trujillo', 'Repuestos varios', 'TRUJILLO', 'Carlos Mendoza', 'TRUJILLO', '2025-02-05', 'CREADO', NULL);

-- =====================================================
-- 6. DETALLE DE SOLICITUDES DE PEDIDO
-- =====================================================

-- Solicitud 1: EPP
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(1, 1, 50, 50, 45.00),
(1, 2, 100, 100, 25.00),
(1, 3, 80, 80, 35.00),
(1, 4, 40, 40, 120.00);

-- Solicitud 2: Repuestos
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(2, 7, 10, 10, 85.00),
(2, 8, 20, 20, 15.00),
(2, 9, 5, 5, 55.00),
(2, 10, 8, 8, 95.00);

-- Solicitud 3: Lubricantes
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(3, 14, 30, NULL, 45.00),
(3, 15, 50, NULL, 12.00);

-- Solicitud 4: Herramientas
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(4, 11, 3, 3, 350.00),
(4, 12, 5, 5, 120.00),
(4, 13, 10, 10, 45.00);

-- Solicitud 5: EPP (rechazada)
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(5, 1, 30, NULL, 45.00),
(5, 2, 50, NULL, 25.00);

-- Solicitud 6: Materiales perforación
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(6, 16, 10, NULL, 250.00),
(6, 17, 8, NULL, 180.00),
(6, 18, 12, NULL, 75.00);

-- Solicitud 7: Varios
INSERT INTO detalle_pedido (solicitud_pedido_id, producto_id, cantidad_solicitada, cantidad_aprobada, precio_referencial) VALUES
(7, 8, 15, NULL, 15.00),
(7, 14, 10, NULL, 45.00);

-- =====================================================
-- 7. COTIZACIONES (CUS05)
-- =====================================================

INSERT INTO cotizaciones (id, numero_cotizacion, solicitud_pedido_id, proveedor_id, fecha_cotizacion, fecha_validez, monto_total, estado, observaciones) VALUES
(1, 'COT-202501001', 1, 6, '2025-01-06', '2025-01-21', 13250.00, 'APROBADO', NULL),
(2, 'COT-202501002', 1, 1, '2025-01-06', '2025-01-21', 12800.00, 'APROBADO', NULL),
(3, 'COT-202501003', 2, 5, '2025-01-11', '2025-01-26', 1895.00, 'APROBADO', NULL),
(4, 'COT-202501004', 2, 1, '2025-01-11', '2025-01-26', 1750.00, 'APROBADO', NULL),
(5, 'COT-202501005', 3, 8, '2025-01-16', '2025-01-31', 1950.00, 'PENDIENTE', NULL),
(6, 'COT-202501006', 4, 3, '2025-01-21', '2025-02-05', 1620.00, 'APROBADO', NULL),
(7, 'COT-202501007', 4, 2, '2025-01-21', '2025-02-05', 1550.00, 'RECHAZADO', 'Precio elevado'),
(8, 'COT-202502001', 6, 2, '2025-02-02', '2025-02-17', 4890.00, 'PENDIENTE', NULL),
(9, 'COT-202502002', 6, 1, '2025-02-02', '2025-02-17', 4750.00, 'PENDIENTE', NULL);

-- =====================================================
-- 8. DETALLE DE COTIZACIONES
-- =====================================================

-- Cotización 1 (3M)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 50, 44.00, 2200.00),
(1, 2, 100, 24.00, 2400.00),
(1, 3, 80, 34.00, 2720.00),
(1, 4, 40, 115.00, 4600.00);

-- Cotización 2 (Ferreyros)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(2, 1, 50, 43.00, 2150.00),
(2, 2, 100, 23.00, 2300.00),
(2, 3, 80, 33.00, 2640.00),
(2, 4, 40, 114.00, 4560.00);

-- Cotización 3 (Repuestos Industriales)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(3, 7, 10, 82.00, 820.00),
(3, 8, 20, 14.50, 290.00),
(3, 9, 5, 53.00, 265.00),
(3, 10, 8, 90.00, 720.00);

-- Cotización 4 (Ferreyros)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(4, 7, 10, 80.00, 800.00),
(4, 8, 20, 14.00, 280.00),
(4, 9, 5, 50.00, 250.00),
(4, 10, 8, 88.00, 704.00);

-- Cotización 5 (Vistony)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(5, 14, 30, 44.00, 1320.00),
(5, 15, 50, 11.50, 575.00);

-- Cotización 6 (Graña y Montero)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(6, 11, 3, 340.00, 1020.00),
(6, 12, 5, 115.00, 575.00),
(6, 13, 10, 43.00, 430.00);

-- Cotización 7 (Komatsu)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(7, 11, 3, 335.00, 1005.00),
(7, 12, 5, 110.00, 550.00),
(7, 13, 10, 42.00, 420.00);

-- Cotización 8 (Komatsu)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(8, 16, 10, 245.00, 2450.00),
(8, 17, 8, 175.00, 1400.00),
(8, 18, 12, 72.00, 864.00);

-- Cotización 9 (Ferreyros)
INSERT INTO detalle_cotizacion (cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(9, 16, 10, 240.00, 2400.00),
(9, 17, 8, 170.00, 1360.00),
(9, 18, 12, 70.00, 840.00);

-- =====================================================
-- 9. ÓRDENES DE COMPRA (CUS06)
-- =====================================================

INSERT INTO ordenes_compra (id, numero_orden, proveedor_id, destino, referencia, fecha, monto_total, estado) VALUES
(1, 'OC-202501001', 1, 'LIMA', 'COTIZACION: COT-202501002', '2025-01-07', 12800.00, 'ENVIADO'),
(2, 'OC-202501002', 1, 'LIMA', 'COTIZACION: COT-202501004', '2025-01-12', 1750.00, 'ENVIADO'),
(3, 'OC-202501003', 3, 'TRUJILLO', 'COTIZACION: COT-202501006', '2025-01-22', 1620.00, 'ENVIADO'),
(4, 'OC-202502001', 2, 'LIMA', 'Solicitud directa', '2025-02-03', 4890.00, 'CREADO');

-- =====================================================
-- 10. DETALLE DE ÓRDENES DE COMPRA
-- =====================================================

INSERT INTO detalle_orden_compra (orden_compra_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 50, 43.00, 2150.00),
(1, 2, 100, 23.00, 2300.00),
(1, 3, 80, 33.00, 2640.00),
(1, 4, 40, 114.00, 4560.00),
(2, 7, 10, 80.00, 800.00),
(2, 8, 20, 14.00, 280.00),
(2, 9, 5, 50.00, 250.00),
(2, 10, 8, 88.00, 704.00),
(3, 11, 3, 340.00, 1020.00),
(3, 12, 5, 115.00, 575.00),
(3, 13, 10, 43.00, 430.00),
(4, 16, 10, 245.00, 2450.00),
(4, 17, 8, 175.00, 1400.00),
(4, 18, 12, 72.00, 864.00);

-- =====================================================
-- 11. RECEPCIONES (CUS12)
-- =====================================================

INSERT INTO recepciones (id, numero_oi, orden_compra_id, numero_guia_remision, numero_factura, fecha_recepcion, almacen, encargado, estado) VALUES
(1, 'OI-202501001', 1, 'GR-001-2025', 'F001-00000001', '2025-01-10 10:30:00', 'LIMA', 'Juan Perez', 'COMPLETADO'),
(2, 'OI-202501002', 2, 'GR-002-2025', 'F001-00000002', '2025-01-15 14:00:00', 'LIMA', 'Juan Perez', 'COMPLETADO'),
(3, 'OI-202501003', 3, 'GR-003-2025', 'F001-00000003', '2025-01-25 09:15:00', 'TRUJILLO', 'Maria Lopez', 'PARCIAL');

-- =====================================================
-- 12. DETALLE DE RECEPCIONES
-- =====================================================

INSERT INTO detalle_recepcion (recepcion_id, producto_id, cantidad_pedida, cantidad_recibida, cantidad_defectuosa, estado_producto, observacion) VALUES
(1, 1, 50, 50, 0, 'BUENO', NULL),
(1, 2, 100, 98, 2, 'DEFECTUOSO', 'Productos con rayaduras'),
(1, 3, 80, 80, 0, 'BUENO', NULL),
(1, 4, 40, 40, 0, 'BUENO', NULL),
(2, 7, 10, 10, 0, 'BUENO', NULL),
(2, 8, 20, 20, 0, 'BUENO', NULL),
(2, 9, 5, 5, 0, 'BUENO', NULL),
(2, 10, 8, 8, 0, 'BUENO', NULL),
(3, 11, 3, 3, 0, 'BUENO', NULL),
(3, 12, 5, 4, 1, 'DEFECTUOSO', 'Juego incompleto'),
(3, 13, 10, 10, 0, 'BUENO', NULL);

-- =====================================================
-- 13. ÓRDENES DE SALIDA (CUS15, CUS16)
-- =====================================================

INSERT INTO ordenes_salida (id, numero_os, nombre_orden, fecha, trasladar_a, operador_almacen, almacen_origen, glosa, total, estado) VALUES
(1, 'OS-202501001', 'Envío EPP a Mina', '2025-01-12', 'MINA', 'Juan Perez', 'LIMA', 'Envío de EPP para nuevo personal', 6200.00, 'APROBADO'),
(2, 'OS-202501002', 'Envío Repuestos', '2025-01-18', 'MINA', 'Juan Perez', 'LIMA', 'Repuestos urgentes para perforadora', 1750.00, 'APROBADO'),
(3, 'OS-202501003', 'Envío Herramientas', '2025-01-26', 'MINA', 'Maria Lopez', 'TRUJILLO', 'Herramientas para taller', 1620.00, 'CREADO');

-- =====================================================
-- 14. DETALLE DE ÓRDENES DE SALIDA
-- =====================================================

INSERT INTO detalle_salida (orden_salida_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 20, 43.00, 860.00),
(1, 2, 50, 23.00, 1150.00),
(1, 3, 30, 33.00, 990.00),
(1, 4, 15, 114.00, 1710.00),
(2, 7, 5, 80.00, 400.00),
(2, 8, 15, 14.00, 210.00),
(2, 9, 3, 50.00, 150.00),
(2, 10, 5, 88.00, 440.00),
(3, 11, 2, 340.00, 680.00),
(3, 12, 3, 115.00, 345.00),
(3, 13, 5, 43.00, 215.00);

-- =====================================================
-- 15. ACTUALIZACIÓN DE STOCK (según recepciones y salidas)
-- =====================================================

-- Producto 1: Stock inicial 120 - 20 = 100
UPDATE productos SET stock_actual = 100 WHERE id = 1;
-- Producto 2: Stock inicial 250 - 50 = 200
UPDATE productos SET stock_actual = 200 WHERE id = 2;
-- Producto 3: Stock inicial 180 - 30 = 150
UPDATE productos SET stock_actual = 150 WHERE id = 3;
-- Producto 4: Stock inicial 95 - 15 = 80
UPDATE productos SET stock_actual = 80 WHERE id = 4;
-- Producto 7: Stock inicial 25 - 5 = 20
UPDATE productos SET stock_actual = 20 WHERE id = 7;
-- Producto 8: Stock inicial 80 - 15 = 65
UPDATE productos SET stock_actual = 65 WHERE id = 8;
-- Producto 9: Stock inicial 15 - 3 = 12
UPDATE productos SET stock_actual = 12 WHERE id = 9;
-- Producto 10: Stock inicial 12 - 5 = 7
UPDATE productos SET stock_actual = 7 WHERE id = 10;
-- Producto 11: Stock inicial 8 - 2 = 6
UPDATE productos SET stock_actual = 6 WHERE id = 11;
-- Producto 12: Stock inicial 12 - 3 = 9
UPDATE productos SET stock_actual = 9 WHERE id = 12;
-- Producto 13: Stock inicial 18 - 5 = 13
UPDATE productos SET stock_actual = 13 WHERE id = 13;

-- =====================================================
-- 16. REGISTROS EN KARDEX
-- =====================================================

-- Entradas por recepciones
INSERT INTO kardex (producto_id, tipo, documento, numero_documento, cantidad, precio_unitario, subtotal, stock_anterior, stock_nuevo, almacen, fecha_movimiento) VALUES
(1, 'ENTRADA', 'OI', 'OI-202501001', 50, 43.00, 2150.00, 120, 170, 'LIMA', '2025-01-10 10:30:00'),
(2, 'ENTRADA', 'OI', 'OI-202501001', 98, 23.00, 2254.00, 250, 348, 'LIMA', '2025-01-10 10:30:00'),
(3, 'ENTRADA', 'OI', 'OI-202501001', 80, 33.00, 2640.00, 180, 260, 'LIMA', '2025-01-10 10:30:00'),
(4, 'ENTRADA', 'OI', 'OI-202501001', 40, 114.00, 4560.00, 95, 135, 'LIMA', '2025-01-10 10:30:00'),
(7, 'ENTRADA', 'OI', 'OI-202501002', 10, 80.00, 800.00, 25, 35, 'LIMA', '2025-01-15 14:00:00'),
(8, 'ENTRADA', 'OI', 'OI-202501002', 20, 14.00, 280.00, 80, 100, 'LIMA', '2025-01-15 14:00:00'),
(9, 'ENTRADA', 'OI', 'OI-202501002', 5, 50.00, 250.00, 15, 20, 'LIMA', '2025-01-15 14:00:00'),
(10, 'ENTRADA', 'OI', 'OI-202501002', 8, 88.00, 704.00, 12, 20, 'LIMA', '2025-01-15 14:00:00'),
(11, 'ENTRADA', 'OI', 'OI-202501003', 3, 340.00, 1020.00, 8, 11, 'TRUJILLO', '2025-01-25 09:15:00'),
(12, 'ENTRADA', 'OI', 'OI-202501003', 4, 115.00, 460.00, 12, 16, 'TRUJILLO', '2025-01-25 09:15:00'),
(13, 'ENTRADA', 'OI', 'OI-202501003', 10, 43.00, 430.00, 18, 28, 'TRUJILLO', '2025-01-25 09:15:00');

-- Salidas por órdenes de salida
INSERT INTO kardex (producto_id, tipo, documento, numero_documento, cantidad, precio_unitario, subtotal, stock_anterior, stock_nuevo, almacen, fecha_movimiento) VALUES
(1, 'SALIDA', 'OS', 'OS-202501001', 20, 43.00, 860.00, 170, 150, 'LIMA', '2025-01-12 00:00:00'),
(2, 'SALIDA', 'OS', 'OS-202501001', 50, 23.00, 1150.00, 348, 298, 'LIMA', '2025-01-12 00:00:00'),
(3, 'SALIDA', 'OS', 'OS-202501001', 30, 33.00, 990.00, 260, 230, 'LIMA', '2025-01-12 00:00:00'),
(4, 'SALIDA', 'OS', 'OS-202501001', 15, 114.00, 1710.00, 135, 120, 'LIMA', '2025-01-12 00:00:00'),
(7, 'SALIDA', 'OS', 'OS-202501002', 5, 80.00, 400.00, 35, 30, 'LIMA', '2025-01-18 00:00:00'),
(8, 'SALIDA', 'OS', 'OS-202501002', 15, 14.00, 210.00, 100, 85, 'LIMA', '2025-01-18 00:00:00'),
(9, 'SALIDA', 'OS', 'OS-202501002', 3, 50.00, 150.00, 20, 17, 'LIMA', '2025-01-18 00:00:00'),
(10, 'SALIDA', 'OS', 'OS-202501002', 5, 88.00, 440.00, 20, 15, 'LIMA', '2025-01-18 00:00:00');

-- Actualizar stock final después de movimientos
UPDATE productos SET stock_actual = 150 WHERE id = 1;
UPDATE productos SET stock_actual = 298 WHERE id = 2;
UPDATE productos SET stock_actual = 230 WHERE id = 3;
UPDATE productos SET stock_actual = 120 WHERE id = 4;
UPDATE productos SET stock_actual = 30 WHERE id = 7;
UPDATE productos SET stock_actual = 85 WHERE id = 8;
UPDATE productos SET stock_actual = 17 WHERE id = 9;
UPDATE productos SET stock_actual = 15 WHERE id = 10;
UPDATE productos SET stock_actual = 11 WHERE id = 11;
UPDATE productos SET stock_actual = 16 WHERE id = 12;
UPDATE productos SET stock_actual = 28 WHERE id = 13;

-- =====================================================
-- 17. REGISTROS DE AUDITORÍA (RNF_57)
-- =====================================================

INSERT INTO auditoria (usuario, accion, entidad, detalle, ip, fecha_hora) VALUES
('admin', 'CREAR_USUARIO', 'USUARIO', 'Usuario creado: gerente con rol: GERENTE', '127.0.0.1', '2025-01-01 10:00:00'),
('admin', 'CREAR_USUARIO', 'USUARIO', 'Usuario creado: jefe_almacen con rol: JEFE_ALMACEN', '127.0.0.1', '2025-01-01 10:05:00'),
('admin', 'CREAR_USUARIO', 'USUARIO', 'Usuario creado: asistente_compras con rol: ASISTENTE_COMPRAS', '127.0.0.1', '2025-01-01 10:10:00'),
('gerente', 'LOGIN_EXITOSO', 'USUARIO', 'Usuario autenticado: gerente', '192.168.1.100', '2025-01-05 08:00:00'),
('gerente', 'APROBAR_SOLICITUD', 'SOLICITUD_PEDIDO', 'Solicitud aprobada: PED-202501001', '192.168.1.100', '2025-01-05 09:00:00'),
('asistente_compras', 'CREAR_COTIZACION', 'COTIZACION', 'Cotización creada: COT-202501001 para proveedor: 3M Perú', '192.168.1.101', '2025-01-06 10:00:00'),
('gerente', 'APROBAR_COTIZACION', 'COTIZACION', 'Cotización aprobada: COT-202501002', '192.168.1.100', '2025-01-07 09:00:00'),
('asistente_compras', 'CREAR_ORDEN_COMPRA', 'ORDEN_COMPRA', 'Orden de compra creada: OC-202501001 para proveedor: Ferreyros S.A.', '192.168.1.101', '2025-01-07 10:00:00'),
('asistente_almacen', 'REGISTRAR_RECEPCION', 'RECEPCION', 'Recepción registrada: OI-202501001 para OC: OC-202501001', '192.168.1.102', '2025-01-10 10:30:00'),
('jefe_almacen', 'APROBAR_ORDEN_SALIDA', 'ORDEN_SALIDA', 'Orden de salida aprobada: OS-202501001', '192.168.1.103', '2025-01-12 08:00:00');

-- =====================================================
-- FIN DE DATOS DE PRUEBA
-- =====================================================
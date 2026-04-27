-- ============================================================
-- Datos iniciales de ejemplo (opcional)
-- Los usuarios con BCrypt se crean automaticamente mediante
-- DataInitializer.java al primer arranque.
-- ============================================================
USE minero_logistica;

-- Productos de ejemplo
INSERT INTO productos (codigo, nombre, descripcion, categoria, unidad_medida,
                       precio_referencial, stock_minimo, stock_actual,
                       stock_lima, stock_trujillo, stock_mina, activo)
VALUES
  ('PRD-001', 'Casco Minero con Lampara', 'Casco de seguridad con lampara LED integrada',
     'EPP', 'UND', 85.50, 10, 50, 20, 15, 15, 1),
  ('PRD-002', 'Guantes Industriales de Cuero', 'Guantes reforzados para operaciones pesadas',
     'EPP', 'PAR', 25.00, 20, 120, 40, 40, 40, 1),
  ('PRD-003', 'Botas de Seguridad con Punta Acero', 'Botas dielectricas con punta reforzada',
     'EPP', 'PAR', 150.00, 15, 80, 30, 25, 25, 1),
  ('PRD-004', 'Broca para Perforacion 3/4 pulgada', 'Broca de acero tungsteno para perforacion',
     'PERFORACION', 'UND', 180.00, 5, 30, 10, 10, 10, 1),
  ('PRD-005', 'Aceite Hidraulico ISO 68', 'Aceite para sistemas hidraulicos de maquinaria pesada',
     'LUBRICANTES', 'GLN', 95.00, 10, 40, 15, 10, 15, 1),
  ('PRD-006', 'Mazo de Acero 5 kg', 'Mazo de acero forjado para trabajo pesado',
     'HERRAMIENTAS', 'UND', 120.00, 8, 25, 10, 8, 7, 1),
  ('PRD-007', 'Uniforme Reflectivo Completo', 'Uniforme tipo overall con bandas reflectivas',
     'EPP', 'UND', 220.00, 15, 60, 20, 20, 20, 1),
  ('PRD-008', 'Explosivo ANFO', 'Agente explosivo para voladura controlada',
     'PERFORACION', 'KG', 8.50, 100, 500, 0, 0, 500, 1);

-- Proveedores de ejemplo
INSERT INTO proveedores (codigo, razon_social, ruc, nombre_contacto, telefono,
                         email, direccion, estado, prioridad, puntaje_evaluacion,
                         fecha_registro)
VALUES
  ('PROV-001', 'Minera Tauro S.A.C.', '20123456789', 'Maria Gonzales',
     '987654321', 'ventas@tauro.com', 'Av. Industrial 123, Lima', 'ACTIVO', 1, 45.0, NOW()),
  ('PROV-002', 'Suministros del Sur S.A.', '20987654321', 'Carlos Mendoza',
     '988112233', 'ventas@suministrosur.com', 'Calle Arequipa 456, Arequipa', 'ACTIVO', 2, 38.0, NOW()),
  ('PROV-003', 'Equipos Mineros EIRL', '10456789012', 'Ana Torres',
     '955778899', 'info@equiposmineros.com', 'Jr. Los Andes 789, Trujillo', 'ACTIVO', 3, 28.0, NOW()),
  ('PROV-004', 'EPP del Peru S.A.C.', '20555666777', 'Luis Vargas',
     '922334455', 'ventas@eppdelperu.com', 'Av. Javier Prado 2020, San Borja', 'ACTIVO', 1, 47.0, NOW()),
  ('PROV-005', 'Perforaciones Andinas S.A.', '20111222333', 'Jose Rivera',
     '933445566', 'proyectos@perforandinas.com', 'Av. Minera 101, Huancayo', 'ACTIVO', 2, 40.0, NOW());

-- Nota: Los usuarios NO se insertan aqui. Se crean automaticamente
-- con passwords cifrados en BCrypt mediante el componente DataInitializer
-- al primer arranque de Spring Boot.

-- ============================================================
-- SISTEMA LOGISTICO MINERO - Script de creacion de BD
-- Curso: Desarrollo de Aplicaciones Web I (4694) - CIBERTEC
-- ============================================================
-- Nota: Este script es OPCIONAL. Spring Boot con
-- spring.jpa.hibernate.ddl-auto=update crea las tablas
-- automaticamente al iniciar la aplicacion.
-- ============================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS minero_logistica
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE minero_logistica;

-- ============================================================
-- Usuario dedicado (opcional, mas seguro que usar root)
-- ============================================================
-- CREATE USER 'minero_app'@'localhost' IDENTIFIED BY 'minero2026';
-- GRANT ALL PRIVILEGES ON minero_logistica.* TO 'minero_app'@'localhost';
-- FLUSH PRIVILEGES;

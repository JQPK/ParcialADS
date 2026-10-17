-- =====================================================================
-- TechServices Perú — Examen Parcial N.° 01
-- Archivo: database/schema.sql
-- Propósito: Creación de la base de datos bd_techservices y sus tablas.
-- Compatible con: MySQL 8.0+ / phpMyAdmin / MariaDB 10+
-- =====================================================================

-- 1. Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS bd_techservices
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bd_techservices;

-- 2. Eliminar tablas previas si existieran para permitir inicialización limpia
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS usuarios_api;

-- 3. Tabla usuarios_api (Requisito E1.02)
-- Contiene: ID, nombre del usuario, API Key, Token de autenticación, tokens disponibles y estado.
CREATE TABLE usuarios_api (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    api_key VARCHAR(100) NOT NULL UNIQUE,
    token_autenticacion VARCHAR(255) NOT NULL,
    tokens_disponibles INT NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_tokens_no_negativos CHECK (tokens_disponibles >= 0),
    CONSTRAINT chk_estado_valido CHECK (estado IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice para búsqueda eficiente por credenciales compuestas
CREATE INDEX idx_usuarios_api_auth ON usuarios_api (api_key, token_autenticacion, estado);

-- 4. Tabla clientes (Requisito E1.03)
-- Contiene: ID, nombres, apellidos, DNI, correo electrónico y teléfono.
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni CHAR(8) NOT NULL UNIQUE,
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice para búsquedas por DNI
CREATE INDEX idx_clientes_dni ON clientes (dni);

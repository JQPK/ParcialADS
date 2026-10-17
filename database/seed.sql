-- =====================================================================
-- TechServices Perú — Examen Parcial N.° 01
-- Archivo: database/seed.sql
-- Propósito: Carga de datos demostrativos reproducibles (Requisito E1.04).
-- Al menos dos usuarios autorizados y al menos cinco clientes.
-- =====================================================================

-- En entornos locales: USE bd_techservices;
-- En Clever Cloud / Nube: Asegúrate de tener seleccionada tu base de datos en el panel izquierdo.

-- 1. Insertar usuarios autorizados para consumir la API
-- Usuario 1: Credenciales oficiales del enunciado del examen (10 tokens iniciales)
-- Usuario 2: Segundo usuario autorizado activo (5 tokens iniciales)
-- Usuario 3: Usuario activo sin tokens (0 tokens) para probar HTTP 429
-- Usuario 4: Usuario deshabilitado/inactivo para probar HTTP 403
INSERT INTO usuarios_api (nombre_usuario, api_key, token_autenticacion, tokens_disponibles, estado) VALUES
('admin_demo',       'MI_API_KEY_2026',    'TOKEN_SEGURO_123456',    10, 'ACTIVO'),
('empresa_consultas', 'TECH_KEY_987654',    'TOKEN_EMPRESA_SEC_2026',  5, 'ACTIVO'),
('usuario_sin_saldo', 'KEY_SIN_TOKENS_001', 'TOKEN_SIN_SALDO_000000',  0, 'ACTIVO'),
('usuario_inactivo',  'KEY_INACTIVO_999',   'TOKEN_INACTIVO_999999',  10, 'INACTIVO')
ON DUPLICATE KEY UPDATE
    tokens_disponibles = VALUES(tokens_disponibles),
    estado = VALUES(estado);

-- 2. Insertar al menos 5 clientes representativos con datos válidos
INSERT INTO clientes (id, nombres, apellidos, dni, correo_electronico, telefono) VALUES
(1, 'Ana', 'Torres Quispe', '12345678', 'ana.torres@techservices.pe', '999888777'),
(2, 'Carlos Alberto', 'Mendoza Vargas', '87654321', 'carlos.mendoza@gmail.com', '987654321'),
(3, 'María Elena', 'Rojas Castro', '45678912', 'maria.rojas@empresa.com', '912345678'),
(4, 'Jorge Luis', 'Chávez Huamán', '32165498', 'jorge.chavez@outlook.com', '954321987'),
(5, 'Lucía Fernanda', 'Paredes Silva', '78912345', 'lucia.paredes@hotmail.com', '932145687'),
(6, 'Diego Alonso', 'Flores Morales', '65498732', 'diego.flores@techservices.pe', '965412389')
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    correo_electronico = VALUES(correo_electronico),
    telefono = VALUES(telefono);

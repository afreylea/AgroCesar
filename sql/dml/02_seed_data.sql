-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DML: 02_seed_data.sql
--  Motor: Oracle XE 18c / 21c
--  Versión: 1.0
--
--  Ejecutar DESPUÉS de 01_create_tables.sql.
--
--  Credenciales de prueba:
--    admin@agrocesar.com       / admin1234
--    agricultor@cesar.com      / agricultor1234
--    agricultora@astrea.com    / agri456
-- ============================================================


-- ============================================================
--  1. MUNICIPIOS DEL CESAR
--     Coordenadas reales para Open-Meteo
-- ============================================================
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Valledupar',           10.4800, -73.2500);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Agustín Codazzi',      10.0369, -73.2315);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Astrea',                9.3667, -74.2000);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Becerril',              9.7333, -73.7833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Bosconia',             10.3667, -73.3500);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Chimichagua',           9.2667, -74.3167);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Chiriguaná',           10.1500, -73.6333);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Curumaní',              9.2000, -73.6833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('El Copey',             10.2333, -73.5167);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('La Gloria',             8.4833, -74.3167);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('La Jagua de Ibirico',   9.3833, -74.3167);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Manaure',              10.3167, -73.2333);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Pailitas',              8.6667, -73.5667);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Pelaya',                8.0333, -73.9500);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Pueblo Bello',         10.4167, -73.5833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Río de Oro',            8.3833, -73.3833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('San Alberto',           9.0167, -73.9333);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('San Diego',            10.3167, -73.4833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('San Martín',            8.0667, -73.4833);
INSERT INTO MUNICIPIOS (NOMBRE, LATITUD, LONGITUD) VALUES ('Tamalameque',           8.8667, -74.3167);

COMMIT;


-- ============================================================
--  2. USUARIOS
--     Hashes BCrypt $2b$12$ generados con rounds=12.
--     Compatibles con Spring Security BCryptPasswordEncoder.
--
--     admin@agrocesar.com       / admin1234
--     agricultor@cesar.com      / agricultor1234
--     agricultora@astrea.com    / agri456
-- ============================================================

-- Admin del sistema
INSERT INTO USUARIOS (ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, MUNICIPIO_ID, TELEFONO, ACTIVO)
VALUES (
    'Administrador del Sistema',
    'admin@agrocesar.com',
    '$2b$12$OA6RhTTL8o7vBdwxnq.5gOvv3CtibBsT0zVgVMdb74ezX6qkNLium',
    'ADMIN',
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Valledupar'),
    '3008001001',
    1
);

-- Agricultor 1 — residente en Valledupar
-- Tiene cultivos en DOS municipios distintos para probar pronósticos diferenciados
INSERT INTO USUARIOS (ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, MUNICIPIO_ID, TELEFONO, ACTIVO)
VALUES (
    'Juan Pérez',
    'agricultor@cesar.com',
    '$2b$12$RRONJqWoDFJ4ue4n8WDgx.PMwpNC06rrGsV9llcWxOLWRMUaRwLfy',
    'AGRICULTOR',
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Valledupar'),
    '3001234567',
    1
);

-- Agricultor 2 — INACTIVO para probar que findByEmailAndActivo la rechaza
INSERT INTO USUARIOS (ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, MUNICIPIO_ID, TELEFONO, ACTIVO)
VALUES (
    'María González',
    'agricultora@astrea.com',
    '$2b$12$27JdeEySOhco5jpWARAasukAaUDsdIb7jXIjpm3kfwlaTAXr3YMlW',
    'AGRICULTOR',
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Astrea'),
    '3009876543',
    0
);

COMMIT;


-- ============================================================
--  3. CATÁLOGO DE CULTIVOS
--     Datos reales IDEAM / FAO / ICA
--     Fuente: Plan de desarrollo Agrocesar v3.0
-- ============================================================

INSERT INTO CULTIVOS_CATALOGO (
    NOMBRE, DESCRIPCION, CATEGORIA,
    TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
    HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
    DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO
) VALUES (
    'Maíz',
    'Maíz amarillo duro para grano seco. Cultivo transitorio principal del Cesar.',
    'TRANSITORIO', 10, 35, 2, 80, 50, 90,
    'Franco-arcilloso', 90, 120, 'IDEAM/FAO', 1
);

INSERT INTO CULTIVOS_CATALOGO (
    NOMBRE, DESCRIPCION, CATEGORIA,
    TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
    HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
    DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO
) VALUES (
    'Arroz',
    'Arroz de temporal. Sensible a sequía prolongada y exceso de lluvia en cosecha.',
    'TRANSITORIO', 15, 38, 5, 120, 60, 95,
    'Arcilloso', 120, 150, 'FEDEARROZ/IDEAM', 1
);

INSERT INTO CULTIVOS_CATALOGO (
    NOMBRE, DESCRIPCION, CATEGORIA,
    TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
    HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
    DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO
) VALUES (
    'Yuca',
    'Manihot esculenta. Tolerante a sequía moderada pero sensible al exceso de humedad.',
    'TRANSITORIO', 18, 40, 3, 100, 55, 85,
    'Franco-arenoso', 180, 240, 'AGROSAVIA/ICA', 1
);

-- DIAS_COSECHA_MAX = 730 (2 años) conforme al plan v3.0
INSERT INTO CULTIVOS_CATALOGO (
    NOMBRE, DESCRIPCION, CATEGORIA,
    TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
    HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
    DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO
) VALUES (
    'Palma de aceite',
    'Elaeis guineensis. Cultivo permanente insignia del Cesar. Cosecha continua todo el año.',
    'PERMANENTE', 22, 38, 4, 150, 65, 90,
    'Franco', 365, 730, 'Fedepalma/FAO', 1
);

COMMIT;


-- ============================================================
--  4. CULTIVOS DEL AGRICULTOR
--     Juan tiene cultivos en DOS municipios distintos →
--     verifica que el motor genera alertas con pronósticos
--     diferenciados por municipio del cultivo (no del usuario).
-- ============================================================

-- Juan — Maíz en Valledupar (sin overrides, hereda del catálogo)
-- 45 días sembrado → días restantes ≈ 60-75 → severidad MEDIA en TRANSITORIO
INSERT INTO CULTIVOS_AGRICULTOR (
    USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
    HECTAREAS, FECHA_SIEMBRA, ACTIVO
) VALUES (
    (SELECT ID FROM USUARIOS  WHERE EMAIL  = 'agricultor@cesar.com'),
    (SELECT ID FROM CULTIVOS_CATALOGO WHERE NOMBRE = 'Maíz'),
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Valledupar'),
    5.5, SYSDATE - 45, 1
);

-- Juan — Arroz en Agustín Codazzi (municipio distinto al de residencia)
-- 20 días sembrado → días restantes ≈ 110-130 → severidad BAJA en TRANSITORIO
INSERT INTO CULTIVOS_AGRICULTOR (
    USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
    HECTAREAS, FECHA_SIEMBRA, ACTIVO
) VALUES (
    (SELECT ID FROM USUARIOS  WHERE EMAIL  = 'agricultor@cesar.com'),
    (SELECT ID FROM CULTIVOS_CATALOGO WHERE NOMBRE = 'Arroz'),
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Agustín Codazzi'),
    3.0, SYSDATE - 20, 1
);

-- Yuca de María en Astrea con override TEMP_MAX = 37°C
-- Verifica NVL(37, 40) = 37 en V_CULTIVOS_CON_UMBRALES
-- El motor NO debe procesar este cultivo porque María está INACTIVA (ACTIVO=0)
INSERT INTO CULTIVOS_AGRICULTOR (
    USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
    HECTAREAS, FECHA_SIEMBRA,
    TEMP_MAX_OVERRIDE, ACTIVO
) VALUES (
    (SELECT ID FROM USUARIOS  WHERE EMAIL  = 'agricultora@astrea.com'),
    (SELECT ID FROM CULTIVOS_CATALOGO WHERE NOMBRE = 'Yuca'),
    (SELECT ID FROM MUNICIPIOS WHERE NOMBRE = 'Astrea'),
    12.0, SYSDATE - 120, 37, 1
);

COMMIT;


-- ============================================================
--  VERIFICACIÓN POST-EJECUCIÓN
-- ============================================================

-- Conteos esperados
SELECT 'MUNICIPIOS: '       || COUNT(*) AS RESULTADO FROM MUNICIPIOS
UNION ALL
SELECT 'USUARIOS activos: ' || COUNT(*) FROM USUARIOS WHERE ACTIVO = 1
UNION ALL
SELECT 'USUARIOS totales: ' || COUNT(*) FROM USUARIOS
UNION ALL
SELECT 'CATALOGO: '         || COUNT(*) FROM CULTIVOS_CATALOGO
UNION ALL
SELECT 'CULTIVOS AGR: '     || COUNT(*) FROM CULTIVOS_AGRICULTOR;

-- V_CULTIVOS_CON_UMBRALES debe mostrar solo 2 filas (Juan, usuario activo)
-- La Yuca de María no debe aparecer porque su usuario está INACTIVO
SELECT ID, AGRICULTOR, CULTIVO, MUNICIPIO,
       TEMP_MAX_EFECTIVA, DIAS_RESTANTES
FROM V_CULTIVOS_CON_UMBRALES;

-- Verifica que el override de Yuca aplica correctamente
-- TEMP_MAX_EFECTIVA debe ser 37 (override) y no 40 (catálogo)
-- Esta query devuelve 0 filas porque María está inactiva —
-- confirma que el filtro WHERE u.ACTIVO = 1 funciona
SELECT COUNT(*) AS FILAS_YUCA_INACTIVA
FROM V_CULTIVOS_CON_UMBRALES
WHERE CULTIVO = 'Yuca';

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
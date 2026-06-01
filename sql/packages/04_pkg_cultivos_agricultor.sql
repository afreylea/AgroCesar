-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 04_pkg_cultivos_agricultor.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_CULTIVOS_AGRICULTOR — CRUD de CULTIVOS_AGRICULTOR
--
--  Convención de parámetros:
--    p_<nombre>  — parámetro de entrada (IN)
--    p_<nombre>  — parámetro de salida (OUT) cuando se indica OUT
--    p_cursor    — SYS_REFCURSOR de salida para consultas (OUT)
--
--  Manejo de errores:
--    Cada procedure de escritura propaga la excepción al llamador
--    (Spring / JDBI). El manejo transaccional se hace desde Java.
-- ============================================================


SET DEFINE OFF
SET SQLBLANKLINES ON

-- ============================================================
--  4. PKG_CULTIVOS_AGRICULTOR
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_CULTIVOS_AGRICULTOR AS

    -- Consultas
    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_id_and_usuario(
        p_id         IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    );

    -- Escritura
    PROCEDURE prc_insert(
        p_usuario_id       IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_catalogo_id      IN  CULTIVOS_AGRICULTOR.CATALOGO_ID%TYPE,
        p_municipio_id     IN  CULTIVOS_AGRICULTOR.MUNICIPIO_ID%TYPE,
        p_hectareas        IN  CULTIVOS_AGRICULTOR.HECTAREAS%TYPE,
        p_fecha_siembra    IN  CULTIVOS_AGRICULTOR.FECHA_SIEMBRA%TYPE,
        p_latitud_cultivo  IN  CULTIVOS_AGRICULTOR.LATITUD_CULTIVO%TYPE,
        p_longitud_cultivo IN CULTIVOS_AGRICULTOR.LONGITUD_CULTIVO%TYPE,
        p_tipo_suelo       IN  CULTIVOS_AGRICULTOR.TIPO_SUELO%TYPE
    );

    PROCEDURE prc_update(
        p_id                   IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id           IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_hectareas            IN  CULTIVOS_AGRICULTOR.HECTAREAS%TYPE,
        p_temp_min_override    IN  CULTIVOS_AGRICULTOR.TEMP_MIN_OVERRIDE%TYPE,
        p_temp_max_override    IN  CULTIVOS_AGRICULTOR.TEMP_MAX_OVERRIDE%TYPE,
        p_lluvia_min_override  IN  CULTIVOS_AGRICULTOR.LLUVIA_MIN_OVERRIDE%TYPE,
        p_lluvia_max_override  IN  CULTIVOS_AGRICULTOR.LLUVIA_MAX_OVERRIDE%TYPE,
        p_humedad_min_override IN CULTIVOS_AGRICULTOR.HUMEDAD_MIN_OVERRIDE%TYPE,
        p_humedad_max_override IN CULTIVOS_AGRICULTOR.HUMEDAD_MAX_OVERRIDE%TYPE,
        p_rows_updated         OUT NUMBER
    );

    PROCEDURE prc_deactivate(
        p_id           IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id   IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_rows_updated OUT NUMBER
    );

END PKG_CULTIVOS_AGRICULTOR;
/

CREATE OR REPLACE PACKAGE BODY PKG_CULTIVOS_AGRICULTOR AS

    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
                   HECTAREAS, FECHA_SIEMBRA,
                   TEMP_MIN_OVERRIDE, TEMP_MAX_OVERRIDE,
                   LLUVIA_MIN_OVERRIDE, LLUVIA_MAX_OVERRIDE,
                   HUMEDAD_MIN_OVERRIDE, HUMEDAD_MAX_OVERRIDE,
                   LATITUD_CULTIVO, LONGITUD_CULTIVO,
                   TIPO_SUELO, ACTIVO,
                   FECHA_CREACION, FECHA_ACTUALIZACION
            FROM CULTIVOS_AGRICULTOR
            WHERE USUARIO_ID = p_usuario_id
              AND ACTIVO = 1;
    END prc_find_by_usuario;

    PROCEDURE prc_find_by_id_and_usuario(
        p_id         IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
                   HECTAREAS, FECHA_SIEMBRA,
                   TEMP_MIN_OVERRIDE, TEMP_MAX_OVERRIDE,
                   LLUVIA_MIN_OVERRIDE, LLUVIA_MAX_OVERRIDE,
                   HUMEDAD_MIN_OVERRIDE, HUMEDAD_MAX_OVERRIDE,
                   LATITUD_CULTIVO, LONGITUD_CULTIVO,
                   TIPO_SUELO, ACTIVO,
                   FECHA_CREACION, FECHA_ACTUALIZACION
            FROM CULTIVOS_AGRICULTOR
            WHERE ID         = p_id
              AND USUARIO_ID = p_usuario_id
              AND ACTIVO     = 1;
    END prc_find_by_id_and_usuario;

    PROCEDURE prc_insert(
        p_usuario_id       IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_catalogo_id      IN  CULTIVOS_AGRICULTOR.CATALOGO_ID%TYPE,
        p_municipio_id     IN  CULTIVOS_AGRICULTOR.MUNICIPIO_ID%TYPE,
        p_hectareas        IN  CULTIVOS_AGRICULTOR.HECTAREAS%TYPE,
        p_fecha_siembra    IN  CULTIVOS_AGRICULTOR.FECHA_SIEMBRA%TYPE,
        p_latitud_cultivo  IN  CULTIVOS_AGRICULTOR.LATITUD_CULTIVO%TYPE,
        p_longitud_cultivo IN  CULTIVOS_AGRICULTOR.LONGITUD_CULTIVO%TYPE,
        p_tipo_suelo       IN  CULTIVOS_AGRICULTOR.TIPO_SUELO%TYPE
    ) IS
    BEGIN
        INSERT INTO CULTIVOS_AGRICULTOR (
            USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID,
            HECTAREAS, FECHA_SIEMBRA,
            LATITUD_CULTIVO, LONGITUD_CULTIVO,
            TIPO_SUELO, ACTIVO, FECHA_CREACION
        ) VALUES (
            p_usuario_id, p_catalogo_id, p_municipio_id,
            p_hectareas, p_fecha_siembra,
            p_latitud_cultivo, p_longitud_cultivo,
            p_tipo_suelo, 1, SYSDATE
        );
    END prc_insert;

    PROCEDURE prc_update(
        p_id                   IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id           IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_hectareas            IN  CULTIVOS_AGRICULTOR.HECTAREAS%TYPE,
        p_temp_min_override    IN  CULTIVOS_AGRICULTOR.TEMP_MIN_OVERRIDE%TYPE,
        p_temp_max_override    IN  CULTIVOS_AGRICULTOR.TEMP_MAX_OVERRIDE%TYPE,
        p_lluvia_min_override  IN  CULTIVOS_AGRICULTOR.LLUVIA_MIN_OVERRIDE%TYPE,
        p_lluvia_max_override  IN  CULTIVOS_AGRICULTOR.LLUVIA_MAX_OVERRIDE%TYPE,
        p_humedad_min_override IN  CULTIVOS_AGRICULTOR.HUMEDAD_MIN_OVERRIDE%TYPE,
        p_humedad_max_override IN  CULTIVOS_AGRICULTOR.HUMEDAD_MAX_OVERRIDE%TYPE,
        p_rows_updated         OUT NUMBER
    ) IS
    BEGIN
        -- TRG_CULTAGR_FECHA_ACT actualiza FECHA_ACTUALIZACION automáticamente.
        UPDATE CULTIVOS_AGRICULTOR
        SET HECTAREAS            = p_hectareas,
            TEMP_MIN_OVERRIDE    = p_temp_min_override,
            TEMP_MAX_OVERRIDE    = p_temp_max_override,
            LLUVIA_MIN_OVERRIDE  = p_lluvia_min_override,
            LLUVIA_MAX_OVERRIDE  = p_lluvia_max_override,
            HUMEDAD_MIN_OVERRIDE = p_humedad_min_override,
            HUMEDAD_MAX_OVERRIDE = p_humedad_max_override
        WHERE ID         = p_id
          AND USUARIO_ID = p_usuario_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_update;

    PROCEDURE prc_deactivate(
        p_id           IN  CULTIVOS_AGRICULTOR.ID%TYPE,
        p_usuario_id   IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE CULTIVOS_AGRICULTOR
        SET ACTIVO = 0
        WHERE ID         = p_id
          AND USUARIO_ID = p_usuario_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_deactivate;

END PKG_CULTIVOS_AGRICULTOR;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
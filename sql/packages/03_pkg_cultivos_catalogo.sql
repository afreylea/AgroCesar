-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 03_pkg_cultivos_catalogo.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_CATALOGO            — CRUD de CULTIVOS_CATALOGO
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
--  3. PKG_CATALOGO
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_CATALOGO AS

    -- Consultas
    PROCEDURE prc_find_by_id(
        p_id     IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_all_activos(
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    );

    -- Escritura
    PROCEDURE prc_insert(
        p_nombre          IN  CULTIVOS_CATALOGO.NOMBRE%TYPE,
        p_descripcion     IN  CULTIVOS_CATALOGO.DESCRIPCION%TYPE,
        p_categoria       IN  CULTIVOS_CATALOGO.CATEGORIA%TYPE,
        p_temp_min        IN  CULTIVOS_CATALOGO.TEMP_MIN%TYPE,
        p_temp_max        IN  CULTIVOS_CATALOGO.TEMP_MAX%TYPE,
        p_lluvia_min      IN  CULTIVOS_CATALOGO.LLUVIA_MIN%TYPE,
        p_lluvia_max      IN  CULTIVOS_CATALOGO.LLUVIA_MAX%TYPE,
        p_humedad_min     IN  CULTIVOS_CATALOGO.HUMEDAD_MIN%TYPE,
        p_humedad_max     IN  CULTIVOS_CATALOGO.HUMEDAD_MAX%TYPE,
        p_tipo_suelo      IN  CULTIVOS_CATALOGO.TIPO_SUELO%TYPE,
        p_dias_min        IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MIN%TYPE,
        p_dias_max        IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MAX%TYPE,
        p_fuente_datos    IN  CULTIVOS_CATALOGO.FUENTE_DATOS%TYPE,
        p_imagen_url      IN  CULTIVOS_CATALOGO.IMAGEN_URL%TYPE
    );

    PROCEDURE prc_update(
        p_id              IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_nombre          IN  CULTIVOS_CATALOGO.NOMBRE%TYPE,
        p_descripcion     IN  CULTIVOS_CATALOGO.DESCRIPCION%TYPE,
        p_categoria       IN  CULTIVOS_CATALOGO.CATEGORIA%TYPE,
        p_temp_min        IN  CULTIVOS_CATALOGO.TEMP_MIN%TYPE,
        p_temp_max        IN  CULTIVOS_CATALOGO.TEMP_MAX%TYPE,
        p_lluvia_min      IN  CULTIVOS_CATALOGO.LLUVIA_MIN%TYPE,
        p_lluvia_max      IN  CULTIVOS_CATALOGO.LLUVIA_MAX%TYPE,
        p_humedad_min     IN  CULTIVOS_CATALOGO.HUMEDAD_MIN%TYPE,
        p_humedad_max     IN  CULTIVOS_CATALOGO.HUMEDAD_MAX%TYPE,
        p_tipo_suelo      IN  CULTIVOS_CATALOGO.TIPO_SUELO%TYPE,
        p_dias_min        IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MIN%TYPE,
        p_dias_max        IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MAX%TYPE,
        p_fuente_datos    IN  CULTIVOS_CATALOGO.FUENTE_DATOS%TYPE,
        p_imagen_url      IN  CULTIVOS_CATALOGO.IMAGEN_URL%TYPE,
        p_rows_updated    OUT NUMBER
    );

    PROCEDURE prc_desactivar(
        p_id           IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_rows_updated OUT NUMBER
    );

    PROCEDURE prc_activar(
        p_id           IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_rows_updated OUT NUMBER
    );

END PKG_CATALOGO;
/

CREATE OR REPLACE PACKAGE BODY PKG_CATALOGO AS

    PROCEDURE prc_find_by_id(
        p_id     IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA,
                   TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
                   HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX,
                   FUENTE_DATOS, IMAGEN_URL,
                   ACTIVO, FECHA_CREACION, FECHA_ACTUALIZACION
            FROM CULTIVOS_CATALOGO
            WHERE ID = p_id;
    END prc_find_by_id;

    PROCEDURE prc_find_all_activos(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA,
                   TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
                   HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX,
                   FUENTE_DATOS, IMAGEN_URL,
                   ACTIVO, FECHA_CREACION, FECHA_ACTUALIZACION
            FROM CULTIVOS_CATALOGO
            WHERE ACTIVO = 1
            ORDER BY NOMBRE;
    END prc_find_all_activos;

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA,
                   TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
                   HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX,
                   FUENTE_DATOS, IMAGEN_URL,
                   ACTIVO, FECHA_CREACION, FECHA_ACTUALIZACION
            FROM CULTIVOS_CATALOGO
            ORDER BY NOMBRE;
    END prc_find_all;

    PROCEDURE prc_insert(
        p_nombre       IN  CULTIVOS_CATALOGO.NOMBRE%TYPE,
        p_descripcion  IN  CULTIVOS_CATALOGO.DESCRIPCION%TYPE,
        p_categoria    IN  CULTIVOS_CATALOGO.CATEGORIA%TYPE,
        p_temp_min     IN  CULTIVOS_CATALOGO.TEMP_MIN%TYPE,
        p_temp_max     IN  CULTIVOS_CATALOGO.TEMP_MAX%TYPE,
        p_lluvia_min   IN  CULTIVOS_CATALOGO.LLUVIA_MIN%TYPE,
        p_lluvia_max   IN  CULTIVOS_CATALOGO.LLUVIA_MAX%TYPE,
        p_humedad_min  IN  CULTIVOS_CATALOGO.HUMEDAD_MIN%TYPE,
        p_humedad_max  IN  CULTIVOS_CATALOGO.HUMEDAD_MAX%TYPE,
        p_tipo_suelo   IN  CULTIVOS_CATALOGO.TIPO_SUELO%TYPE,
        p_dias_min     IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MIN%TYPE,
        p_dias_max     IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MAX%TYPE,
        p_fuente_datos IN  CULTIVOS_CATALOGO.FUENTE_DATOS%TYPE,
        p_imagen_url   IN  CULTIVOS_CATALOGO.IMAGEN_URL%TYPE
    ) IS
    BEGIN
        INSERT INTO CULTIVOS_CATALOGO (
            NOMBRE, DESCRIPCION, CATEGORIA,
            TEMP_MIN, TEMP_MAX, LLUVIA_MIN, LLUVIA_MAX,
            HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO,
            DIAS_COSECHA_MIN, DIAS_COSECHA_MAX,
            FUENTE_DATOS, IMAGEN_URL
        ) VALUES (
            p_nombre, p_descripcion, p_categoria,
            p_temp_min, p_temp_max, p_lluvia_min, p_lluvia_max,
            p_humedad_min, p_humedad_max, p_tipo_suelo,
            p_dias_min, p_dias_max,
            p_fuente_datos, p_imagen_url
        );
    END prc_insert;

    PROCEDURE prc_update(
        p_id           IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_nombre       IN  CULTIVOS_CATALOGO.NOMBRE%TYPE,
        p_descripcion  IN  CULTIVOS_CATALOGO.DESCRIPCION%TYPE,
        p_categoria    IN  CULTIVOS_CATALOGO.CATEGORIA%TYPE,
        p_temp_min     IN  CULTIVOS_CATALOGO.TEMP_MIN%TYPE,
        p_temp_max     IN  CULTIVOS_CATALOGO.TEMP_MAX%TYPE,
        p_lluvia_min   IN  CULTIVOS_CATALOGO.LLUVIA_MIN%TYPE,
        p_lluvia_max   IN  CULTIVOS_CATALOGO.LLUVIA_MAX%TYPE,
        p_humedad_min  IN  CULTIVOS_CATALOGO.HUMEDAD_MIN%TYPE,
        p_humedad_max  IN  CULTIVOS_CATALOGO.HUMEDAD_MAX%TYPE,
        p_tipo_suelo   IN  CULTIVOS_CATALOGO.TIPO_SUELO%TYPE,
        p_dias_min     IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MIN%TYPE,
        p_dias_max     IN  CULTIVOS_CATALOGO.DIAS_COSECHA_MAX%TYPE,
        p_fuente_datos IN  CULTIVOS_CATALOGO.FUENTE_DATOS%TYPE,
        p_imagen_url   IN  CULTIVOS_CATALOGO.IMAGEN_URL%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        -- TRG_CATALOGO_FECHA_ACT actualiza FECHA_ACTUALIZACION automáticamente.
        UPDATE CULTIVOS_CATALOGO
        SET NOMBRE          = p_nombre,
            DESCRIPCION     = p_descripcion,
            CATEGORIA       = p_categoria,
            TEMP_MIN        = p_temp_min,
            TEMP_MAX        = p_temp_max,
            LLUVIA_MIN      = p_lluvia_min,
            LLUVIA_MAX      = p_lluvia_max,
            HUMEDAD_MIN     = p_humedad_min,
            HUMEDAD_MAX     = p_humedad_max,
            TIPO_SUELO      = p_tipo_suelo,
            DIAS_COSECHA_MIN = p_dias_min,
            DIAS_COSECHA_MAX = p_dias_max,
            FUENTE_DATOS    = p_fuente_datos,
            IMAGEN_URL      = p_imagen_url
        WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_update;

    PROCEDURE prc_desactivar(
        p_id           IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        -- TRG_CATALOGO_DESACTIVAR_CASCADE marca ACTIVO = 0
        -- en CULTIVOS_AGRICULTOR asociados automáticamente.
        UPDATE CULTIVOS_CATALOGO SET ACTIVO = 0 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_desactivar;

    PROCEDURE prc_activar(
        p_id           IN  CULTIVOS_CATALOGO.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE CULTIVOS_CATALOGO SET ACTIVO = 1 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_activar;

END PKG_CATALOGO;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
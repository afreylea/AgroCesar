-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 01_pkg_municipios.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_MUNICIPIOS          — CRUD de MUNICIPIOS
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
--  1. PKG_MUNICIPIOS
-- ============================================================
 
CREATE OR REPLACE PACKAGE PKG_MUNICIPIOS AS
 
    -- Consultas
    PROCEDURE prc_find_by_id(
        p_id       IN  MUNICIPIOS.ID%TYPE,
        p_cursor   OUT SYS_REFCURSOR
    );
 
    PROCEDURE prc_find_by_nombre(
        p_nombre   IN  VARCHAR2,
        p_cursor   OUT SYS_REFCURSOR
    );
 
    PROCEDURE prc_find_by_departamento(
        p_departamento IN VARCHAR2,
        p_cursor       OUT SYS_REFCURSOR
    );
 
    PROCEDURE prc_find_activos(
        p_cursor OUT SYS_REFCURSOR
    );
 
    PROCEDURE prc_find_inactivos(
        p_cursor OUT SYS_REFCURSOR
    );
 
    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    );
 
    -- Escritura
    PROCEDURE prc_insert(
        p_nombre       IN  MUNICIPIOS.NOMBRE%TYPE,
        p_departamento IN  MUNICIPIOS.DEPARTAMENTO%TYPE,
        p_latitud      IN  MUNICIPIOS.LATITUD%TYPE,
        p_longitud     IN  MUNICIPIOS.LONGITUD%TYPE,
        p_activo       IN  MUNICIPIOS.ACTIVO%TYPE
    );
 
    PROCEDURE prc_update(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_nombre       IN  MUNICIPIOS.NOMBRE%TYPE,
        p_departamento IN  MUNICIPIOS.DEPARTAMENTO%TYPE,
        p_latitud      IN  MUNICIPIOS.LATITUD%TYPE,
        p_longitud     IN  MUNICIPIOS.LONGITUD%TYPE,
        p_activo       IN  MUNICIPIOS.ACTIVO%TYPE,
        p_rows_updated OUT NUMBER
    );
 
    PROCEDURE prc_desactivar(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    );
 
    PROCEDURE prc_activar(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    );
 
END PKG_MUNICIPIOS;
/
 
CREATE OR REPLACE PACKAGE BODY PKG_MUNICIPIOS AS
 
    PROCEDURE prc_find_by_id(
        p_id     IN  MUNICIPIOS.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            WHERE ID = p_id;
    END prc_find_by_id;
 
    PROCEDURE prc_find_by_nombre(
        p_nombre IN  VARCHAR2,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        -- p_nombre debe venir con % desde Java: UPPER('%VALLEDUPAR%')
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            WHERE UPPER(NOMBRE) LIKE p_nombre;
    END prc_find_by_nombre;
 
    PROCEDURE prc_find_by_departamento(
        p_departamento IN  VARCHAR2,
        p_cursor       OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            WHERE UPPER(DEPARTAMENTO) = p_departamento
            ORDER BY NOMBRE;
    END prc_find_by_departamento;
 
    PROCEDURE prc_find_activos(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            WHERE ACTIVO = 1
            ORDER BY NOMBRE;
    END prc_find_activos;
 
    PROCEDURE prc_find_inactivos(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            WHERE ACTIVO = 0
            ORDER BY NOMBRE;
    END prc_find_inactivos;
 
    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
                   ACTIVO, FECHA_CREACION
            FROM MUNICIPIOS
            ORDER BY NOMBRE;
    END prc_find_all;
 
    PROCEDURE prc_insert(
        p_nombre       IN  MUNICIPIOS.NOMBRE%TYPE,
        p_departamento IN  MUNICIPIOS.DEPARTAMENTO%TYPE,
        p_latitud      IN  MUNICIPIOS.LATITUD%TYPE,
        p_longitud     IN  MUNICIPIOS.LONGITUD%TYPE,
        p_activo       IN  MUNICIPIOS.ACTIVO%TYPE
    ) IS
    BEGIN
        INSERT INTO MUNICIPIOS (NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD, ACTIVO)
        VALUES (p_nombre, p_departamento, p_latitud, p_longitud, p_activo);
    END prc_insert;
 
    PROCEDURE prc_update(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_nombre       IN  MUNICIPIOS.NOMBRE%TYPE,
        p_departamento IN  MUNICIPIOS.DEPARTAMENTO%TYPE,
        p_latitud      IN  MUNICIPIOS.LATITUD%TYPE,
        p_longitud     IN  MUNICIPIOS.LONGITUD%TYPE,
        p_activo       IN  MUNICIPIOS.ACTIVO%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE MUNICIPIOS
        SET NOMBRE       = p_nombre,
            DEPARTAMENTO = p_departamento,
            LATITUD      = p_latitud,
            LONGITUD     = p_longitud,
            ACTIVO       = p_activo
        WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_update;
 
    PROCEDURE prc_desactivar(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE MUNICIPIOS SET ACTIVO = 0 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_desactivar;
 
    PROCEDURE prc_activar(
        p_id           IN  MUNICIPIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE MUNICIPIOS SET ACTIVO = 1 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_activar;
 
END PKG_MUNICIPIOS;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
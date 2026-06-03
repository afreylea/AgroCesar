-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 06_pkg_cultivos_con_umbrales.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_CULTIVOS_UMBRALES   — Lectura de V_CULTIVOS_CON_UMBRALES + ranking
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
--  6. PKG_CULTIVOS_UMBRALES
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_CULTIVOS_UMBRALES AS

    -- Consultas sobre V_CULTIVOS_CON_UMBRALES
    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_id(
        p_id     IN  NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    );

    -- Ranking de cultivos para el popup del dashboard
    PROCEDURE prc_ranking_cultivos(
        p_cursor OUT SYS_REFCURSOR
    );

END PKG_CULTIVOS_UMBRALES;
/

CREATE OR REPLACE PACKAGE BODY PKG_CULTIVOS_UMBRALES AS

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, USUARIO_ID, AGRICULTOR, TELEFONO,
                   CULTIVO, CATEGORIA,
                   MUNICIPIO_ID, MUNICIPIO,
                   LATITUD, LONGITUD,
                   HECTAREAS, FECHA_SIEMBRA,
                   TEMP_MIN_EFECTIVA, TEMP_MAX_EFECTIVA,
                   LLUVIA_MIN_EFECTIVA, LLUVIA_MAX_EFECTIVA,
                   HUMEDAD_MIN_EFECTIVA, HUMEDAD_MAX_EFECTIVA,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, DIAS_COSECHA_PROM,
                   DIAS_RESTANTES, ACTIVO
            FROM V_CULTIVOS_CON_UMBRALES
            WHERE ACTIVO = 1;
    END prc_find_all;

    PROCEDURE prc_find_by_id(
        p_id     IN  NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, USUARIO_ID, AGRICULTOR, TELEFONO,
                   CULTIVO, CATEGORIA,
                   MUNICIPIO_ID, MUNICIPIO,
                   LATITUD, LONGITUD,
                   HECTAREAS, FECHA_SIEMBRA,
                   TEMP_MIN_EFECTIVA, TEMP_MAX_EFECTIVA,
                   LLUVIA_MIN_EFECTIVA, LLUVIA_MAX_EFECTIVA,
                   HUMEDAD_MIN_EFECTIVA, HUMEDAD_MAX_EFECTIVA,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, DIAS_COSECHA_PROM,
                   DIAS_RESTANTES, ACTIVO
            FROM V_CULTIVOS_CON_UMBRALES
            WHERE ID     = p_id
              AND ACTIVO = 1;
    END prc_find_by_id;

    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, USUARIO_ID, AGRICULTOR, TELEFONO,
                   CULTIVO, CATEGORIA,
                   MUNICIPIO_ID, MUNICIPIO,
                   LATITUD, LONGITUD,
                   HECTAREAS, FECHA_SIEMBRA,
                   TEMP_MIN_EFECTIVA, TEMP_MAX_EFECTIVA,
                   LLUVIA_MIN_EFECTIVA, LLUVIA_MAX_EFECTIVA,
                   HUMEDAD_MIN_EFECTIVA, HUMEDAD_MAX_EFECTIVA,
                   DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, DIAS_COSECHA_PROM,
                   DIAS_RESTANTES, ACTIVO
            FROM V_CULTIVOS_CON_UMBRALES
            WHERE USUARIO_ID = p_usuario_id
              AND ACTIVO     = 1;
    END prc_find_by_usuario;

    PROCEDURE prc_ranking_cultivos(
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT CULTIVO                    AS NOMBRE,
               COUNT(DISTINCT USUARIO_ID) AS TOTAL_AGRICULTORES,
               SUM(HECTAREAS)             AS TOTAL_HECTAREAS,
               (SELECT v2.MUNICIPIO
                FROM V_CULTIVOS_CON_UMBRALES v2
                WHERE v2.CULTIVO = v.CULTIVO
                  AND v2.ACTIVO  = 1
                GROUP BY v2.MUNICIPIO
                ORDER BY SUM(v2.HECTAREAS) DESC
                FETCH FIRST 1 ROW ONLY)  AS MUNICIPIO_PRINCIPAL
        FROM V_CULTIVOS_CON_UMBRALES v
        WHERE ACTIVO = 1
        GROUP BY CULTIVO
        ORDER BY TOTAL_AGRICULTORES DESC;
END prc_ranking_cultivos;

END PKG_CULTIVOS_UMBRALES;
/


-- ============================================================
--  VERIFICACIÓN DE COMPILACIÓN
--  Ejecutar después del script para confirmar que no hay errores.
-- ============================================================

-- Muestra paquetes inválidos (debe devolver 0 filas si todo compiló bien)
SELECT OBJECT_NAME, OBJECT_TYPE, STATUS
FROM USER_OBJECTS
WHERE OBJECT_TYPE IN ('PACKAGE', 'PACKAGE BODY')
  AND STATUS = 'INVALID'
ORDER BY OBJECT_TYPE, OBJECT_NAME;

-- Muestra todos los paquetes creados con su estado
SELECT OBJECT_NAME, OBJECT_TYPE, STATUS, LAST_DDL_TIME
FROM USER_OBJECTS
WHERE OBJECT_TYPE IN ('PACKAGE', 'PACKAGE BODY')
ORDER BY OBJECT_TYPE, OBJECT_NAME;


-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 05_pkg_alertas.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_ALERTAS             — Escritura/lectura de ALERTAS + V_ALERTAS
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
--  5. PKG_ALERTAS
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_ALERTAS AS

    -- Escritura (tabla ALERTAS)
    PROCEDURE prc_insert(
        p_cultivo_agricultor_id IN  ALERTAS.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_alerta           IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_severidad             IN  ALERTAS.SEVERIDAD%TYPE,
        p_descripcion           IN  ALERTAS.DESCRIPCION%TYPE,
        p_recomendacion         IN  ALERTAS.RECOMENDACION%TYPE,
        p_fecha_dia_pronostico  IN  ALERTAS.FECHA_DIA_PRONOSTICO%TYPE,
        p_valor_detectado       IN  ALERTAS.VALOR_DETECTADO%TYPE,
        p_valor_umbral          IN  ALERTAS.VALOR_UMBRAL%TYPE
    );

    PROCEDURE prc_actualizar_recomendacion(
        p_id            IN  ALERTAS.ID%TYPE,
        p_recomendacion IN  ALERTAS.RECOMENDACION%TYPE,
        p_rows_updated  OUT NUMBER
    );

    PROCEDURE prc_marcar_leida(
        p_id           IN  ALERTAS.ID%TYPE,
        p_rows_updated OUT NUMBER
    );

    -- Lectura (vista V_ALERTAS)
    PROCEDURE prc_find_by_id(
        p_id     IN  ALERTAS.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_no_leidas_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_municipio(
        p_municipio_id IN  NUMBER,
        p_cursor       OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_catalogo(
        p_catalogo_id IN  NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_cultivo(
        p_cultivo_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_usuario_and_cultivo(
        p_usuario_id IN  NUMBER,
        p_cultivo_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_tipo(
        p_tipo_alerta IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_usuario_and_tipo(
        p_usuario_id  IN  NUMBER,
        p_tipo_alerta IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_severidad(
        p_severidad IN  ALERTAS.SEVERIDAD%TYPE,
        p_cursor    OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_usuario_and_severidad(
        p_usuario_id IN  NUMBER,
        p_severidad  IN  ALERTAS.SEVERIDAD%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_rango_fechas(
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_cultivos_mas_afectados(
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE prc_count_activas(
        p_count OUT NUMBER
    );

    PROCEDURE prc_count_criticas(
        p_count OUT NUMBER
    );

END PKG_ALERTAS;
/

CREATE OR REPLACE PACKAGE BODY PKG_ALERTAS AS

    PROCEDURE prc_insert(
        p_cultivo_agricultor_id IN  ALERTAS.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_alerta           IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_severidad             IN  ALERTAS.SEVERIDAD%TYPE,
        p_descripcion           IN  ALERTAS.DESCRIPCION%TYPE,
        p_recomendacion         IN  ALERTAS.RECOMENDACION%TYPE,
        p_fecha_dia_pronostico  IN  ALERTAS.FECHA_DIA_PRONOSTICO%TYPE,
        p_valor_detectado       IN  ALERTAS.VALOR_DETECTADO%TYPE,
        p_valor_umbral          IN  ALERTAS.VALOR_UMBRAL%TYPE
    ) IS
    BEGIN
        INSERT INTO ALERTAS (
            CULTIVO_AGRICULTOR_ID, TIPO_ALERTA, SEVERIDAD,
            DESCRIPCION, RECOMENDACION,
            FECHA_DIA_PRONOSTICO, VALOR_DETECTADO, VALOR_UMBRAL
        ) VALUES (
            p_cultivo_agricultor_id, p_tipo_alerta, p_severidad,
            p_descripcion, p_recomendacion,
            p_fecha_dia_pronostico, p_valor_detectado, p_valor_umbral
        );
    END prc_insert;

    PROCEDURE prc_actualizar_recomendacion(
        p_id            IN  ALERTAS.ID%TYPE,
        p_recomendacion IN  ALERTAS.RECOMENDACION%TYPE,
        p_rows_updated  OUT NUMBER
    ) IS
    BEGIN
        UPDATE ALERTAS
        SET RECOMENDACION = p_recomendacion
        WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_actualizar_recomendacion;

    PROCEDURE prc_marcar_leida(
        p_id           IN  ALERTAS.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        -- TRG_ALERTAS_FECHA_LECTURA registra FECHA_LECTURA automáticamente.
        UPDATE ALERTAS SET LEIDA = 1 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_marcar_leida;

    PROCEDURE prc_find_by_id(
        p_id     IN  ALERTAS.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE ALERTA_ID = p_id;
    END prc_find_by_id;

    PROCEDURE prc_find_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE USUARIO_ID = p_usuario_id
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_usuario;

    PROCEDURE prc_find_no_leidas_by_usuario(
        p_usuario_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE USUARIO_ID = p_usuario_id
              AND LEIDA = 0
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_no_leidas_by_usuario;

    PROCEDURE prc_find_by_municipio(
        p_municipio_id IN  NUMBER,
        p_cursor       OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE MUNICIPIO_ID = p_municipio_id
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_municipio;

    PROCEDURE prc_find_by_catalogo(
        p_catalogo_id IN  NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE CATALOGO_ID = p_catalogo_id
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_catalogo;

    PROCEDURE prc_find_by_cultivo(
        p_cultivo_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE CULTIVO_AGRICULTOR_ID = p_cultivo_id
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_cultivo;

    PROCEDURE prc_find_by_usuario_and_cultivo(
        p_usuario_id IN  NUMBER,
        p_cultivo_id IN  NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE USUARIO_ID            = p_usuario_id
              AND CULTIVO_AGRICULTOR_ID = p_cultivo_id
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_usuario_and_cultivo;

    PROCEDURE prc_find_by_tipo(
        p_tipo_alerta IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE TIPO_ALERTA = p_tipo_alerta
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_tipo;

    PROCEDURE prc_find_by_usuario_and_tipo(
        p_usuario_id  IN  NUMBER,
        p_tipo_alerta IN  ALERTAS.TIPO_ALERTA%TYPE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE USUARIO_ID  = p_usuario_id
              AND TIPO_ALERTA = p_tipo_alerta
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_usuario_and_tipo;

    PROCEDURE prc_find_by_severidad(
        p_severidad IN  ALERTAS.SEVERIDAD%TYPE,
        p_cursor    OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE SEVERIDAD = p_severidad
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_severidad;

    PROCEDURE prc_find_by_usuario_and_severidad(
        p_usuario_id IN  NUMBER,
        p_severidad  IN  ALERTAS.SEVERIDAD%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE USUARIO_ID = p_usuario_id
              AND SEVERIDAD   = p_severidad
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_by_usuario_and_severidad;

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            ORDER BY FECHA_GENERACION DESC;
    END prc_find_all;

    PROCEDURE prc_find_by_rango_fechas(
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM V_ALERTAS
            WHERE FECHA_DIA_PRONOSTICO BETWEEN p_fecha_desde AND p_fecha_hasta
            ORDER BY FECHA_DIA_PRONOSTICO DESC;
    END prc_find_by_rango_fechas;

    PROCEDURE prc_find_cultivos_mas_afectados(
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT CULTIVO      AS NOMBRECULTIVO,
                   MUNICIPIO,
                   COUNT(*)     AS TOTALALERTAS
            FROM V_ALERTAS
            WHERE FECHA_DIA_PRONOSTICO BETWEEN p_fecha_desde AND p_fecha_hasta
            GROUP BY CULTIVO, MUNICIPIO
            ORDER BY COUNT(*) DESC;
    END prc_find_cultivos_mas_afectados;

    PROCEDURE prc_count_activas(
        p_count OUT NUMBER
    ) IS
    BEGIN
        SELECT COUNT(*) INTO p_count
        FROM ALERTAS
        WHERE LEIDA = 0;
    END prc_count_activas;

    PROCEDURE prc_count_criticas(
        p_count OUT NUMBER
    ) IS
    BEGIN
        SELECT COUNT(*) INTO p_count
        FROM ALERTAS
        WHERE LEIDA = 0
          AND SEVERIDAD = 'ALTA';
    END prc_count_criticas;

END PKG_ALERTAS;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
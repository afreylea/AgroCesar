-- ============================================================
--  AGROCESAR — Actualizacion Bitacora
--  Script: 09_bitacora_update.sql
--  Motor: Oracle XE 18c / 21c
--  Ejecutar DESPUES de 08_pkg_bitacora.sql
-- ============================================================


-- ============================================================
--  1. COLUMNAS NUEVAS EN BITACORA_CULTIVO
-- ============================================================
ALTER TABLE BITACORA_CULTIVO ADD (
    RESPONSABLE VARCHAR2(60),
    UBICACION   VARCHAR2(100)
);


-- ============================================================
--  2. PKG_BITACORA — SPEC ACTUALIZADA
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_BITACORA AS

    -- Lista entradas de un cultivo, ordenadas por fecha desc
    PROCEDURE listar_por_cultivo(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_cursor                OUT SYS_REFCURSOR
    );

    -- Lista entradas de un agricultor (todos sus cultivos)
    PROCEDURE listar_por_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    );

    -- Lista entradas de un agricultor filtradas por rango de fechas
    PROCEDURE listar_por_usuario_rango(
        p_usuario_id  IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    );

    -- Busca una entrada por ID
    PROCEDURE buscar_por_id(
        p_id     IN  BITACORA_CULTIVO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Inserta una nueva entrada
    PROCEDURE insertar(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable           IN  BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion             IN  BITACORA_CULTIVO.UBICACION%TYPE
    );

    -- Actualiza una entrada existente
    PROCEDURE actualizar(
        p_id                IN BITACORA_CULTIVO.ID%TYPE,
        p_tipo_actividad_id IN BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id         IN BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion       IN BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad   IN BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable       IN BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion         IN BITACORA_CULTIVO.UBICACION%TYPE,
        p_rows_updated      OUT NUMBER
    );

    -- Elimina una entrada por ID
    PROCEDURE eliminar(
        p_id           IN  BITACORA_CULTIVO.ID%TYPE,
        p_rows_deleted OUT NUMBER
    );

END PKG_BITACORA;
/


-- ============================================================
--  3. PKG_BITACORA — BODY ACTUALIZADO
-- ============================================================
CREATE OR REPLACE PACKAGE BODY PKG_BITACORA AS

    -- --------------------------------------------------------
    --  Listar por cultivo
    -- --------------------------------------------------------
    PROCEDURE listar_por_cultivo(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_cursor                OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                b.ID,
                b.CULTIVO_AGRICULTOR_ID,
                b.TIPO_ACTIVIDAD_ID,
                cc.NOMBRE       AS CULTIVO,
                ta.NOMBRE       AS TIPO_NOMBRE,
                ta.ICONO        AS TIPO_ICONO,
                b.ALERTA_ID,
                b.DESCRIPCION,
                b.FECHA_ACTIVIDAD,
                b.FECHA_CREACION,
                b.RESPONSABLE,
                b.UBICACION
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  b.CULTIVO_AGRICULTOR_ID = p_cultivo_agricultor_id
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_cultivo;

    -- --------------------------------------------------------
    --  Listar por usuario
    -- --------------------------------------------------------
    PROCEDURE listar_por_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                b.ID,
                b.CULTIVO_AGRICULTOR_ID,
                b.TIPO_ACTIVIDAD_ID,
                cc.NOMBRE       AS CULTIVO,
                ta.NOMBRE       AS TIPO_NOMBRE,
                ta.ICONO        AS TIPO_ICONO,
                b.ALERTA_ID,
                b.DESCRIPCION,
                b.FECHA_ACTIVIDAD,
                b.FECHA_CREACION,
                b.RESPONSABLE,
                b.UBICACION
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  ca.USUARIO_ID = p_usuario_id
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_usuario;

    -- --------------------------------------------------------
    --  Listar por usuario con rango de fechas
    -- --------------------------------------------------------
    PROCEDURE listar_por_usuario_rango(
        p_usuario_id  IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                b.ID,
                b.CULTIVO_AGRICULTOR_ID,
                b.TIPO_ACTIVIDAD_ID,
                cc.NOMBRE       AS CULTIVO,
                ta.NOMBRE       AS TIPO_NOMBRE,
                ta.ICONO        AS TIPO_ICONO,
                b.ALERTA_ID,
                b.DESCRIPCION,
                b.FECHA_ACTIVIDAD,
                b.FECHA_CREACION,
                b.RESPONSABLE,
                b.UBICACION
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  ca.USUARIO_ID = p_usuario_id
              AND  TRUNC(b.FECHA_ACTIVIDAD) BETWEEN p_fecha_desde AND p_fecha_hasta
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_usuario_rango;

    -- --------------------------------------------------------
    --  Buscar por ID
    -- --------------------------------------------------------
    PROCEDURE buscar_por_id(
        p_id     IN  BITACORA_CULTIVO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                b.ID,
                b.CULTIVO_AGRICULTOR_ID,
                b.TIPO_ACTIVIDAD_ID,
                cc.NOMBRE       AS CULTIVO,
                ta.NOMBRE       AS TIPO_NOMBRE,
                ta.ICONO        AS TIPO_ICONO,
                b.ALERTA_ID,
                b.DESCRIPCION,
                b.FECHA_ACTIVIDAD,
                b.FECHA_CREACION,
                b.RESPONSABLE,
                b.UBICACION
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  b.ID = p_id;
    END buscar_por_id;

    -- --------------------------------------------------------
    --  Insertar
    -- --------------------------------------------------------
    PROCEDURE insertar(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable           IN  BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion             IN  BITACORA_CULTIVO.UBICACION%TYPE
    ) IS
    BEGIN
        INSERT INTO BITACORA_CULTIVO (
            CULTIVO_AGRICULTOR_ID, TIPO_ACTIVIDAD_ID,
            ALERTA_ID, DESCRIPCION, FECHA_ACTIVIDAD,
            RESPONSABLE, UBICACION
        ) VALUES (
            p_cultivo_agricultor_id, p_tipo_actividad_id,
            p_alerta_id, p_descripcion, p_fecha_actividad,
            p_responsable, p_ubicacion
        );
    END insertar;

    -- --------------------------------------------------------
    --  Actualizar
    -- --------------------------------------------------------
    PROCEDURE actualizar(
        p_id                IN BITACORA_CULTIVO.ID%TYPE,
        p_tipo_actividad_id IN BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id         IN BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion       IN BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad   IN BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable       IN BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion         IN BITACORA_CULTIVO.UBICACION%TYPE,
        p_rows_updated      OUT NUMBER
    ) IS
    BEGIN
        UPDATE BITACORA_CULTIVO
        SET    TIPO_ACTIVIDAD_ID = p_tipo_actividad_id,
               ALERTA_ID         = p_alerta_id,
               DESCRIPCION       = p_descripcion,
               FECHA_ACTIVIDAD   = p_fecha_actividad,
               RESPONSABLE       = p_responsable,
               UBICACION         = p_ubicacion
        WHERE  ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END actualizar;

    -- --------------------------------------------------------
    --  Eliminar
    -- --------------------------------------------------------
    PROCEDURE eliminar(
        p_id           IN  BITACORA_CULTIVO.ID%TYPE,
        p_rows_deleted OUT NUMBER
    ) IS
    BEGIN
        DELETE FROM BITACORA_CULTIVO WHERE ID = p_id;
        p_rows_deleted := SQL%ROWCOUNT;
    END eliminar;

END PKG_BITACORA;
/


-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
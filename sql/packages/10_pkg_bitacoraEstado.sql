-- ============================================================
--  AGROCESAR — Bitacora Estado
--  Script: 10_bitacora_estado.sql
--  Ejecutar DESPUES de 09_bitacora_update.sql
-- ============================================================


-- ============================================================
--  1. COLUMNA ESTADO
-- ============================================================
ALTER TABLE BITACORA_CULTIVO ADD (
    ESTADO VARCHAR2(15) DEFAULT 'REALIZADO' NOT NULL
);

ALTER TABLE BITACORA_CULTIVO ADD CONSTRAINT CK_BITACORA_ESTADO
    CHECK (ESTADO IN ('PLANIFICADO', 'REALIZADO'));


-- ============================================================
--  2. PKG_BITACORA — SPEC ACTUALIZADA
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_BITACORA AS

    PROCEDURE listar_por_cultivo(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_cursor                OUT SYS_REFCURSOR
    );

    PROCEDURE listar_por_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    );

    PROCEDURE listar_por_usuario_rango(
        p_usuario_id  IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE buscar_por_id(
        p_id     IN  BITACORA_CULTIVO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE insertar(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable           IN  BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion             IN  BITACORA_CULTIVO.UBICACION%TYPE,
        p_estado                IN  BITACORA_CULTIVO.ESTADO%TYPE
    );

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

    PROCEDURE cambiar_estado(
        p_id           IN  BITACORA_CULTIVO.ID%TYPE,
        p_estado       IN  BITACORA_CULTIVO.ESTADO%TYPE,
        p_rows_updated OUT NUMBER
    );

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

    PROCEDURE listar_por_cultivo(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_cursor                OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT b.ID, b.CULTIVO_AGRICULTOR_ID, b.TIPO_ACTIVIDAD_ID,
                   cc.NOMBRE AS CULTIVO, ta.NOMBRE AS TIPO_NOMBRE, ta.ICONO AS TIPO_ICONO,
                   b.ALERTA_ID, b.DESCRIPCION, b.FECHA_ACTIVIDAD, b.FECHA_CREACION,
                   b.RESPONSABLE, b.UBICACION, b.ESTADO
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  b.CULTIVO_AGRICULTOR_ID = p_cultivo_agricultor_id
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_cultivo;

    PROCEDURE listar_por_usuario(
        p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT b.ID, b.CULTIVO_AGRICULTOR_ID, b.TIPO_ACTIVIDAD_ID,
                   cc.NOMBRE AS CULTIVO, ta.NOMBRE AS TIPO_NOMBRE, ta.ICONO AS TIPO_ICONO,
                   b.ALERTA_ID, b.DESCRIPCION, b.FECHA_ACTIVIDAD, b.FECHA_CREACION,
                   b.RESPONSABLE, b.UBICACION, b.ESTADO
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  ca.USUARIO_ID = p_usuario_id
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_usuario;

    PROCEDURE listar_por_usuario_rango(
        p_usuario_id  IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
        p_fecha_desde IN  DATE,
        p_fecha_hasta IN  DATE,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT b.ID, b.CULTIVO_AGRICULTOR_ID, b.TIPO_ACTIVIDAD_ID,
                   cc.NOMBRE AS CULTIVO, ta.NOMBRE AS TIPO_NOMBRE, ta.ICONO AS TIPO_ICONO,
                   b.ALERTA_ID, b.DESCRIPCION, b.FECHA_ACTIVIDAD, b.FECHA_CREACION,
                   b.RESPONSABLE, b.UBICACION, b.ESTADO
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  ca.USUARIO_ID = p_usuario_id
              AND  TRUNC(b.FECHA_ACTIVIDAD) BETWEEN p_fecha_desde AND p_fecha_hasta
            ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
    END listar_por_usuario_rango;

    PROCEDURE buscar_por_id(
        p_id     IN  BITACORA_CULTIVO.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT b.ID, b.CULTIVO_AGRICULTOR_ID, b.TIPO_ACTIVIDAD_ID,
                   cc.NOMBRE AS CULTIVO, ta.NOMBRE AS TIPO_NOMBRE, ta.ICONO AS TIPO_ICONO,
                   b.ALERTA_ID, b.DESCRIPCION, b.FECHA_ACTIVIDAD, b.FECHA_CREACION,
                   b.RESPONSABLE, b.UBICACION, b.ESTADO
            FROM   BITACORA_CULTIVO b
            JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
            JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
            JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
            WHERE  b.ID = p_id;
    END buscar_por_id;

    PROCEDURE insertar(
        p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
        p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
        p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
        p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
        p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
        p_responsable           IN  BITACORA_CULTIVO.RESPONSABLE%TYPE,
        p_ubicacion             IN  BITACORA_CULTIVO.UBICACION%TYPE,
        p_estado                IN  BITACORA_CULTIVO.ESTADO%TYPE
    ) IS
    BEGIN
        INSERT INTO BITACORA_CULTIVO (
            CULTIVO_AGRICULTOR_ID, TIPO_ACTIVIDAD_ID,
            ALERTA_ID, DESCRIPCION, FECHA_ACTIVIDAD,
            RESPONSABLE, UBICACION, ESTADO
        ) VALUES (
            p_cultivo_agricultor_id, p_tipo_actividad_id,
            p_alerta_id, p_descripcion, p_fecha_actividad,
            p_responsable, p_ubicacion, p_estado
        );
    END insertar;

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

    PROCEDURE cambiar_estado(
        p_id           IN  BITACORA_CULTIVO.ID%TYPE,
        p_estado       IN  BITACORA_CULTIVO.ESTADO%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE BITACORA_CULTIVO
        SET    ESTADO = p_estado
        WHERE  ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END cambiar_estado;

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
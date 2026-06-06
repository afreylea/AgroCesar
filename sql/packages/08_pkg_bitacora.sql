-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script PL/SQL: 08_pkg_bitacora.sql
--  Motor: Oracle XE 18c / 21c
--
--  Contiene dos paquetes:
--    PKG_TIPOS_ACTIVIDAD  — CRUD catálogo de tipos
--    PKG_BITACORA         — CRUD entradas de la bitácora
--
--  Ejecutar DESPUÉS de 03_bitacora.sql.
-- ============================================================

SET SQLBLANKLINES ON;

-- ============================================================
--  PKG_BITACORA — SPEC
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_BITACORA AS

  -- Lista todas las entradas de un cultivo, ordenadas por fecha desc
  PROCEDURE listar_por_cultivo(
    p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
    p_cursor                OUT SYS_REFCURSOR
  );

  -- Lista todas las entradas de un agricultor (todos sus cultivos)
  PROCEDURE listar_por_usuario(
    p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
    p_cursor     OUT SYS_REFCURSOR
  );

  -- Busca una entrada por ID
  PROCEDURE buscar_por_id(
    p_id     IN  BITACORA_CULTIVO.ID%TYPE,
    p_cursor OUT SYS_REFCURSOR
  );

  -- Inserta una nueva entrada y devuelve el ID generado
  PROCEDURE insertar(
    p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
    p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
    p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
    p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
    p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE
  );

  -- Actualiza descripcion, tipo y fecha de una entrada existente
  PROCEDURE actualizar(
    p_id                IN BITACORA_CULTIVO.ID%TYPE,
    p_tipo_actividad_id IN BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
    p_alerta_id         IN BITACORA_CULTIVO.ALERTA_ID%TYPE,
    p_descripcion       IN BITACORA_CULTIVO.DESCRIPCION%TYPE,
    p_fecha_actividad   IN BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
    p_rows_updated OUT NUMBER
  );

  -- Elimina una entrada por ID (hard delete — el agricultor borra su propio registro)
  PROCEDURE eliminar(
    p_id IN BITACORA_CULTIVO.ID%TYPE,   
    p_rows_deleted OUT NUMBER
  );

END PKG_BITACORA;
/


-- ============================================================
--  PKG_BITACORA — BODY
-- ============================================================
CREATE OR REPLACE PACKAGE BODY PKG_BITACORA AS

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
        ta.NOMBRE  AS TIPO_NOMBRE,
        ta.ICONO   AS TIPO_ICONO,
        b.ALERTA_ID,
        b.DESCRIPCION,
        b.FECHA_ACTIVIDAD,
        b.FECHA_CREACION
      FROM   BITACORA_CULTIVO b
      JOIN   TIPOS_ACTIVIDAD ta ON b.TIPO_ACTIVIDAD_ID = ta.ID
      WHERE  b.CULTIVO_AGRICULTOR_ID = p_cultivo_agricultor_id
      ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
  END listar_por_cultivo;

  PROCEDURE listar_por_usuario(
    p_usuario_id IN  CULTIVOS_AGRICULTOR.USUARIO_ID%TYPE,
    p_cursor     OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT
        b.ID,
        b.CULTIVO_AGRICULTOR_ID,
        cc.NOMBRE  AS CULTIVO,
        b.TIPO_ACTIVIDAD_ID,
        ta.NOMBRE  AS TIPO_NOMBRE,
        ta.ICONO   AS TIPO_ICONO,
        b.ALERTA_ID,
        b.DESCRIPCION,
        b.FECHA_ACTIVIDAD,
        b.FECHA_CREACION
      FROM   BITACORA_CULTIVO b
      JOIN   TIPOS_ACTIVIDAD ta       ON b.TIPO_ACTIVIDAD_ID     = ta.ID
      JOIN   CULTIVOS_AGRICULTOR ca   ON b.CULTIVO_AGRICULTOR_ID = ca.ID
      JOIN   CULTIVOS_CATALOGO cc     ON ca.CATALOGO_ID          = cc.ID
      WHERE  ca.USUARIO_ID = p_usuario_id
      ORDER BY b.FECHA_ACTIVIDAD DESC, b.FECHA_CREACION DESC;
  END listar_por_usuario;

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
        ta.NOMBRE  AS TIPO_NOMBRE,
        ta.ICONO   AS TIPO_ICONO,
        b.ALERTA_ID,
        b.DESCRIPCION,
        b.FECHA_ACTIVIDAD,
        b.FECHA_CREACION
      FROM   BITACORA_CULTIVO b
      JOIN   TIPOS_ACTIVIDAD ta ON b.TIPO_ACTIVIDAD_ID = ta.ID
      WHERE  b.ID = p_id;
  END buscar_por_id;

  PROCEDURE insertar(
    p_cultivo_agricultor_id IN  BITACORA_CULTIVO.CULTIVO_AGRICULTOR_ID%TYPE,
    p_tipo_actividad_id     IN  BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
    p_alerta_id             IN  BITACORA_CULTIVO.ALERTA_ID%TYPE,
    p_descripcion           IN  BITACORA_CULTIVO.DESCRIPCION%TYPE,
    p_fecha_actividad       IN  BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE
  ) IS
  BEGIN
    INSERT INTO BITACORA_CULTIVO (
      CULTIVO_AGRICULTOR_ID, TIPO_ACTIVIDAD_ID,
      ALERTA_ID, DESCRIPCION, FECHA_ACTIVIDAD
    ) VALUES (
      p_cultivo_agricultor_id, p_tipo_actividad_id,
      p_alerta_id, p_descripcion, p_fecha_actividad
    );
  END insertar;

  PROCEDURE actualizar(
    p_id                IN BITACORA_CULTIVO.ID%TYPE,
    p_tipo_actividad_id IN BITACORA_CULTIVO.TIPO_ACTIVIDAD_ID%TYPE,
    p_alerta_id         IN BITACORA_CULTIVO.ALERTA_ID%TYPE,
    p_descripcion       IN BITACORA_CULTIVO.DESCRIPCION%TYPE,
    p_fecha_actividad   IN BITACORA_CULTIVO.FECHA_ACTIVIDAD%TYPE,
    p_rows_updated OUT NUMBER
  ) IS
  BEGIN
    UPDATE BITACORA_CULTIVO
    SET    TIPO_ACTIVIDAD_ID = p_tipo_actividad_id,
           ALERTA_ID         = p_alerta_id,
           DESCRIPCION       = p_descripcion,
           FECHA_ACTIVIDAD   = p_fecha_actividad
    WHERE  ID = p_id;
    p_rows_updated := SQL%ROWCOUNT;
  END actualizar;

  PROCEDURE eliminar(
    p_id IN BITACORA_CULTIVO.ID%TYPE,   
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
-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script PL/SQL: 07_pkg_tipo_alerta.sql
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
--  PKG_TIPOS_ACTIVIDAD — SPEC
-- ============================================================
CREATE OR REPLACE PACKAGE PKG_TIPOS_ACTIVIDAD AS

  -- Devuelve todos los tipos activos (para el select del formulario)
  PROCEDURE listar_activos(p_cursor OUT SYS_REFCURSOR);

  -- Devuelve un tipo por ID
  PROCEDURE buscar_por_id(
    p_id     IN  TIPOS_ACTIVIDAD.ID%TYPE,
    p_cursor OUT SYS_REFCURSOR
  );

  -- Inserta un nuevo tipo y devuelve el ID generado
  PROCEDURE insertar(
    p_nombre IN  TIPOS_ACTIVIDAD.NOMBRE%TYPE,
    p_icono  IN  TIPOS_ACTIVIDAD.ICONO%TYPE
  );

  -- Activa o desactiva un tipo (1 / 0)
  PROCEDURE cambiar_estado(
    p_id     IN TIPOS_ACTIVIDAD.ID%TYPE,
    p_estado IN TIPOS_ACTIVIDAD.ACTIVO%TYPE,
    p_rows_updated OUT NUMBER
  );

END PKG_TIPOS_ACTIVIDAD;
/


CREATE OR REPLACE PACKAGE BODY PKG_TIPOS_ACTIVIDAD AS

  PROCEDURE listar_activos(p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT ID, NOMBRE, ICONO, ACTIVO, FECHA_CREACION
      FROM   TIPOS_ACTIVIDAD
      WHERE  ACTIVO = 1
      ORDER BY NOMBRE;
  END listar_activos;

  PROCEDURE buscar_por_id(
    p_id     IN  TIPOS_ACTIVIDAD.ID%TYPE,
    p_cursor OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT ID, NOMBRE, ICONO, ACTIVO, FECHA_CREACION
      FROM   TIPOS_ACTIVIDAD
      WHERE  ID = p_id;
  END buscar_por_id;

  PROCEDURE insertar(
    p_nombre IN  TIPOS_ACTIVIDAD.NOMBRE%TYPE,
    p_icono  IN  TIPOS_ACTIVIDAD.ICONO%TYPE
  ) IS
  BEGIN
    INSERT INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO)
    VALUES (p_nombre, p_icono);
  END insertar;

  PROCEDURE cambiar_estado(
    p_id     IN TIPOS_ACTIVIDAD.ID%TYPE,
    p_estado IN TIPOS_ACTIVIDAD.ACTIVO%TYPE,
    p_rows_updated OUT NUMBER
  ) IS
  BEGIN
    UPDATE TIPOS_ACTIVIDAD
    SET    ACTIVO = p_estado
    WHERE  ID = p_id;
    p_rows_updated := SQL%ROWCOUNT;
  END cambiar_estado;

END PKG_TIPOS_ACTIVIDAD;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
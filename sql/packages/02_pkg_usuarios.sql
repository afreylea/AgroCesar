-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL/DML: 02_pkg_usuarios.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--
--  Paquetes incluidos:
--    PKG_USUARIOS            — CRUD de USUARIOS
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
--  2. PKG_USUARIOS
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_USUARIOS AS

    -- Consultas
    PROCEDURE prc_find_by_id(
        p_id     IN  USUARIOS.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_email(
        p_email  IN  USUARIOS.EMAIL%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE prc_find_by_email_activo(
        p_email  IN  USUARIOS.EMAIL%TYPE,
        p_cursor OUT SYS_REFCURSOR
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

    PROCEDURE prc_find_by_rol(
        p_rol    IN  USUARIOS.ROL%TYPE,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Escritura
    PROCEDURE prc_insert(
        p_nombre        IN  USUARIOS.NOMBRE%TYPE,
        p_apellido      IN  USUARIOS.APELLIDO%TYPE,
        p_email         IN  USUARIOS.EMAIL%TYPE,
        p_password_hash IN USUARIOS.PASSWORD_HASH%TYPE,
        p_rol           IN  USUARIOS.ROL%TYPE,
        p_municipio_id  IN USUARIOS.MUNICIPIO_ID%TYPE,
        p_telefono      IN  USUARIOS.TELEFONO%TYPE
    );

    PROCEDURE prc_update(
        p_id           IN  USUARIOS.ID%TYPE,
        p_nombre       IN  USUARIOS.NOMBRE%TYPE,
        p_apellido     IN  USUARIOS.APELLIDO%TYPE,
        p_email        IN  USUARIOS.EMAIL%TYPE,
        p_municipio_id IN USUARIOS.MUNICIPIO_ID%TYPE,
        p_telefono     IN  USUARIOS.TELEFONO%TYPE,
        p_rows_updated OUT NUMBER
    );

    PROCEDURE prc_actualizar_ultimo_login(
        p_email IN USUARIOS.EMAIL%TYPE
    );

    PROCEDURE prc_desactivar(
        p_id           IN  USUARIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    );

    PROCEDURE prc_activar(
        p_id           IN  USUARIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    );

END PKG_USUARIOS;
/

CREATE OR REPLACE PACKAGE BODY PKG_USUARIOS AS

    PROCEDURE prc_find_by_id(
        p_id     IN  USUARIOS.ID%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE ID = p_id;
    END prc_find_by_id;

    PROCEDURE prc_find_by_email(
        p_email  IN  USUARIOS.EMAIL%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE EMAIL = p_email;
    END prc_find_by_email;

    PROCEDURE prc_find_by_email_activo(
        p_email  IN  USUARIOS.EMAIL%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE EMAIL = p_email
              AND ACTIVO = 1;
    END prc_find_by_email_activo;

    PROCEDURE prc_find_activos(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE ACTIVO = 1
            ORDER BY NOMBRE;
    END prc_find_activos;

    PROCEDURE prc_find_inactivos(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE ACTIVO = 0
            ORDER BY NOMBRE;
    END prc_find_inactivos;

    PROCEDURE prc_find_all(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            ORDER BY NOMBRE;
    END prc_find_all;

    PROCEDURE prc_find_by_rol(
        p_rol    IN  USUARIOS.ROL%TYPE,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
                   MUNICIPIO_ID, TELEFONO, ACTIVO, FECHA_CREACION, ULTIMO_LOGIN
            FROM USUARIOS
            WHERE ROL = p_rol
              AND ACTIVO = 1
            ORDER BY NOMBRE;
    END prc_find_by_rol;

    PROCEDURE prc_insert(
        p_nombre        IN  USUARIOS.NOMBRE%TYPE,
        p_apellido      IN  USUARIOS.APELLIDO%TYPE,
        p_email         IN  USUARIOS.EMAIL%TYPE,
        p_password_hash IN  USUARIOS.PASSWORD_HASH%TYPE,
        p_rol           IN  USUARIOS.ROL%TYPE,
        p_municipio_id  IN  USUARIOS.MUNICIPIO_ID%TYPE,
        p_telefono      IN  USUARIOS.TELEFONO%TYPE
    ) IS
    BEGIN
        INSERT INTO USUARIOS (
            NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH,
            ROL, MUNICIPIO_ID, TELEFONO
        ) VALUES (
            p_nombre, p_apellido, p_email, p_password_hash,
            p_rol, p_municipio_id, p_telefono
        );
    END prc_insert;

    PROCEDURE prc_update(
        p_id           IN  USUARIOS.ID%TYPE,
        p_nombre       IN  USUARIOS.NOMBRE%TYPE,
        p_apellido     IN  USUARIOS.APELLIDO%TYPE,
        p_email        IN  USUARIOS.EMAIL%TYPE,
        p_municipio_id IN  USUARIOS.MUNICIPIO_ID%TYPE,
        p_telefono     IN  USUARIOS.TELEFONO%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE USUARIOS
        SET NOMBRE       = p_nombre,
            APELLIDO     = p_apellido,
            EMAIL        = p_email,
            MUNICIPIO_ID = p_municipio_id,
            TELEFONO     = p_telefono
        WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_update;

    PROCEDURE prc_actualizar_ultimo_login(
        p_email IN USUARIOS.EMAIL%TYPE
    ) IS
    BEGIN
        UPDATE USUARIOS
        SET ULTIMO_LOGIN = SYSDATE
        WHERE EMAIL = p_email
          AND ACTIVO = 1;
    END prc_actualizar_ultimo_login;

    PROCEDURE prc_desactivar(
        p_id           IN  USUARIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE USUARIOS SET ACTIVO = 0 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_desactivar;

    PROCEDURE prc_activar(
        p_id           IN  USUARIOS.ID%TYPE,
        p_rows_updated OUT NUMBER
    ) IS
    BEGIN
        UPDATE USUARIOS SET ACTIVO = 1 WHERE ID = p_id;
        p_rows_updated := SQL%ROWCOUNT;
    END prc_activar;

END PKG_USUARIOS;
/

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
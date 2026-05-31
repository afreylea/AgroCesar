-- ============================================================
-- Script: 04_alter_usuarios_reset_token.sql
-- Propósito: Agregar columnas para recuperación de contraseña
--            a la tabla USUARIOS.
-- ============================================================

DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*)
INTO   v_count
FROM   user_tab_columns
WHERE  table_name  = 'USUARIOS'
  AND  column_name = 'RESET_TOKEN';

IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            ALTER TABLE USUARIOS ADD (
                RESET_TOKEN         VARCHAR2(64),
                RESET_TOKEN_EXPIRY  TIMESTAMP
            )
        ';
        DBMS_OUTPUT.PUT_LINE('Columnas RESET_TOKEN agregadas correctamente.');
ELSE
        DBMS_OUTPUT.PUT_LINE('Columnas ya existen. Sin cambios.');
END IF;
END;
/
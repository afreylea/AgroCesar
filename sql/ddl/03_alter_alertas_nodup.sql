-- ============================================================
-- Script: 03_alter_alertas_nodup.sql
-- Propósito: Agregar constraint de unicidad UQ_ALERTAS_NODUP
--            a la tabla ALERTAS.
-- ============================================================

DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*)
INTO   v_count
FROM   user_constraints
WHERE  table_name      = 'ALERTAS'
  AND  constraint_name = 'UQ_ALERTAS_NODUP';

IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            ALTER TABLE ALERTAS
            ADD CONSTRAINT UQ_ALERTAS_NODUP
            UNIQUE (cultivo_agricultor_id, tipo_alerta, fecha_dia_pronostico)
        ';
        DBMS_OUTPUT.PUT_LINE('Constraint UQ_ALERTAS_NODUP creado correctamente.');
ELSE
        DBMS_OUTPUT.PUT_LINE('Constraint UQ_ALERTAS_NODUP ya existe. Sin cambios.');
END IF;
END;
/
-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script: 00_drop_packages.sql
--  Motor: Oracle XE 18c / 21c
--
--  Elimina todos los paquetes del proyecto antes de recriarlos.
--  Ejecutar ANTES de los scripts 01 al 06.
--
--  ORDER MATTERS: se eliminan en orden inverso al de creación
--  para respetar dependencias entre paquetes.
-- ============================================================

BEGIN
    FOR p IN (
        SELECT object_name
        FROM   user_objects
        WHERE  object_type = 'PACKAGE'
          AND  object_name IN (
                   'PKG_CULTIVOS_UMBRALES',
                   'PKG_ALERTAS',
                   'PKG_CULTIVOS_AGRICULTOR',
                   'PKG_CATALOGO',
                   'PKG_USUARIOS',
                   'PKG_MUNICIPIOS'
               )
    ) LOOP
        -- DROP PACKAGE elimina la especificación y el body en una sola sentencia
        EXECUTE IMMEDIATE 'DROP PACKAGE ' || p.object_name;
        DBMS_OUTPUT.PUT_LINE('Eliminado: ' || p.object_name);
    END LOOP;
END;
/

-- Verificación: no debe devolver filas si el drop fue exitoso
SELECT object_name, object_type
FROM   user_objects
WHERE  object_type IN ('PACKAGE', 'PACKAGE BODY')
  AND  object_name IN (
           'PKG_CULTIVOS_UMBRALES',
           'PKG_ALERTAS',
           'PKG_CULTIVOS_AGRICULTOR',
           'PKG_CATALOGO',
           'PKG_USUARIOS',
           'PKG_MUNICIPIOS'
       )
ORDER BY object_type, object_name;

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL: 04_triggers.sql
--  Motor: Oracle XE 18c / 21c
-- ============================================================


-- ============================================================
--  Municipios
-- ============================================================
 
CREATE OR REPLACE TRIGGER TRG_MUNICIPIOS_PK
  BEFORE INSERT ON MUNICIPIOS
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    :NEW.ID := SEQ_MUNICIPIOS.NEXTVAL;
  END IF;
END;
/


-- ============================================================
--  Usuarios
-- ============================================================
 
CREATE OR REPLACE TRIGGER TRG_USUARIOS_PK
  BEFORE INSERT ON USUARIOS
  FOR EACH ROW
BEGIN
  :NEW.ID := SEQ_USUARIOS.NEXTVAL;
END;
/


-- ============================================================
-- Cuntivos_Catalogo
-- ============================================================
 
CREATE OR REPLACE TRIGGER TRG_CULTIVOS_CATALOGO_PK
  BEFORE INSERT ON CULTIVOS_CATALOGO
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    :NEW.ID := SEQ_CULTIVOS_CATALOGO.NEXTVAL;
  END IF;
END;
/
 
CREATE OR REPLACE TRIGGER TRG_CATALOGO_FECHA_ACT
  BEFORE UPDATE ON CULTIVOS_CATALOGO
  FOR EACH ROW
BEGIN
  :NEW.FECHA_ACTUALIZACION := SYSDATE;
END;
/


-- ============================================================
--  Cultivos_Agricultor
-- ============================================================
 
CREATE OR REPLACE TRIGGER TRG_CULTIVOS_AGRICULTOR_PK
  BEFORE INSERT ON CULTIVOS_AGRICULTOR
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    :NEW.ID := SEQ_CULTIVOS_AGRICULTOR.NEXTVAL;
  END IF;
END;
/
 
CREATE OR REPLACE TRIGGER TRG_CULTAGR_FECHA_ACT
  BEFORE UPDATE ON CULTIVOS_AGRICULTOR
  FOR EACH ROW
BEGIN
  :NEW.FECHA_ACTUALIZACION := SYSDATE;
END;
/
 
CREATE OR REPLACE TRIGGER TRG_VALIDA_FECHA_SIEMBRA
  BEFORE INSERT OR UPDATE OF FECHA_SIEMBRA ON CULTIVOS_AGRICULTOR
  FOR EACH ROW
BEGIN
  IF :NEW.FECHA_SIEMBRA > TRUNC(SYSDATE) THEN
    RAISE_APPLICATION_ERROR(
      -20010,
      'La fecha de siembra (' ||
      TO_CHAR(:NEW.FECHA_SIEMBRA, 'DD/MM/YYYY') ||
      ') no puede ser una fecha futura. Fecha maxima permitida: ' ||
      TO_CHAR(TRUNC(SYSDATE), 'DD/MM/YYYY')
    );
  END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CATALOGO_DESACTIVAR_CASCADE
  AFTER UPDATE OF ACTIVO ON CULTIVOS_CATALOGO
  FOR EACH ROW
  WHEN (NEW.ACTIVO = 0 AND OLD.ACTIVO = 1)
BEGIN
  UPDATE CULTIVOS_AGRICULTOR
  SET    ACTIVO = 0
  WHERE  CATALOGO_ID = :NEW.ID
  AND    ACTIVO = 1;
END;
/


-- ============================================================
--  Alertas
-- ============================================================
 
CREATE OR REPLACE TRIGGER TRG_ALERTAS_PK
  BEFORE INSERT ON ALERTAS
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    :NEW.ID := SEQ_ALERTAS.NEXTVAL;
  END IF;
END;
/
 
CREATE OR REPLACE TRIGGER TRG_ALERTAS_FECHA_LECTURA
  BEFORE UPDATE OF LEIDA ON ALERTAS
  FOR EACH ROW
  WHEN (NEW.LEIDA = 1 AND OLD.LEIDA = 0)
BEGIN
  :NEW.FECHA_LECTURA := SYSDATE;
END;
/
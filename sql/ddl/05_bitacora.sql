-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL: 05_bitacora.sql
--  Motor: Oracle XE 18c / 21c
--
--  Ejecutar DESPUÉS de 01_create_tables.sql y 02_seed_data.sql.
--  Agrega las tablas TIPOS_ACTIVIDAD y BITACORA_CULTIVO.
-- ============================================================

SET SQLBLANKLINES ON;

-- ============================================================
--  0. LIMPIEZA (solo en desarrollo)
-- ============================================================
BEGIN
  FOR t IN (SELECT table_name FROM user_tables
            WHERE table_name IN ('BITACORA_CULTIVO', 'TIPOS_ACTIVIDAD'))
  LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
  END LOOP;
END;
/

BEGIN
  FOR s IN (SELECT sequence_name FROM user_sequences
            WHERE sequence_name IN ('SEQ_TIPOS_ACTIVIDAD', 'SEQ_BITACORA'))
  LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
  END LOOP;
END;
/


-- ============================================================
--  1. SECUENCIAS
-- ============================================================
CREATE SEQUENCE SEQ_TIPOS_ACTIVIDAD START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_BITACORA        START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ============================================================
--  2. TIPOS_ACTIVIDAD
-- ============================================================
CREATE TABLE TIPOS_ACTIVIDAD (
  ID             NUMBER(10)   NOT NULL,
  NOMBRE         VARCHAR2(30) NOT NULL,
  ICONO          VARCHAR2(31),
  ACTIVO         NUMBER(1)    DEFAULT 1 NOT NULL,
  FECHA_CREACION DATE         DEFAULT SYSDATE NOT NULL,

  CONSTRAINT PK_TIPOS_ACTIVIDAD       PRIMARY KEY (ID),
  CONSTRAINT UQ_TIPOS_ACTIVIDAD_NOMBRE UNIQUE (NOMBRE),
  CONSTRAINT CK_TIPOS_ACTIVO          CHECK (ACTIVO IN (0, 1))
);

CREATE OR REPLACE TRIGGER TRG_TIPOS_ACTIVIDAD_PK
  BEFORE INSERT ON TIPOS_ACTIVIDAD
  FOR EACH ROW
BEGIN
  :NEW.ID := SEQ_TIPOS_ACTIVIDAD.NEXTVAL;
END;
/

CREATE INDEX IDX_TIPOS_ACTIVO ON TIPOS_ACTIVIDAD(ACTIVO);


-- ============================================================
--  3. BITACORA_CULTIVO
-- ============================================================
CREATE TABLE BITACORA_CULTIVO (
  ID                    NUMBER(10)    NOT NULL,
  CULTIVO_AGRICULTOR_ID NUMBER(10)    NOT NULL,
  TIPO_ACTIVIDAD_ID     NUMBER(10)    NOT NULL,
  ALERTA_ID             NUMBER(10),
  DESCRIPCION           VARCHAR2(200),
  FECHA_ACTIVIDAD       DATE          NOT NULL,
  FECHA_CREACION        DATE          DEFAULT SYSDATE NOT NULL,

  CONSTRAINT PK_BITACORA             PRIMARY KEY (ID),
  CONSTRAINT FK_BITACORA_CULTIVO     FOREIGN KEY (CULTIVO_AGRICULTOR_ID)
    REFERENCES CULTIVOS_AGRICULTOR(ID),
  CONSTRAINT FK_BITACORA_TIPO        FOREIGN KEY (TIPO_ACTIVIDAD_ID)
    REFERENCES TIPOS_ACTIVIDAD(ID),
  CONSTRAINT FK_BITACORA_ALERTA      FOREIGN KEY (ALERTA_ID)
    REFERENCES ALERTAS(ID)
);

CREATE OR REPLACE TRIGGER TRG_BITACORA_PK
  BEFORE INSERT ON BITACORA_CULTIVO
  FOR EACH ROW
BEGIN
  :NEW.ID := SEQ_BITACORA.NEXTVAL;
END;
/

CREATE INDEX IDX_BITACORA_CULTIVO ON BITACORA_CULTIVO(CULTIVO_AGRICULTOR_ID);
CREATE INDEX IDX_BITACORA_FECHA   ON BITACORA_CULTIVO(FECHA_ACTIVIDAD);
CREATE INDEX IDX_BITACORA_TIPO    ON BITACORA_CULTIVO(TIPO_ACTIVIDAD_ID);
CREATE INDEX IDX_BITACORA_ALERTA  ON BITACORA_CULTIVO(ALERTA_ID);


-- ============================================================
--  4. SEED — TIPOS_ACTIVIDAD
-- ============================================================
INSERT ALL
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Riego',         'ph-drop',         1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Fertilizacion', 'ph-flask',         1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Fumigacion',    'ph-spray-bottle',  1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Cosecha',       'ph-basket',        1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Poda',          'ph-scissors',      1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Siembra',       'ph-plant',         1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Deshierbe',     'ph-broom',         1)
  INTO TIPOS_ACTIVIDAD (NOMBRE, ICONO, ACTIVO) VALUES ('Otro',          'ph-note',          1)
SELECT 1 FROM DUAL;

COMMIT;


-- ============================================================
--  5. VERIFICACIÓN POST-EJECUCIÓN
-- ============================================================
SELECT 'TIPOS_ACTIVIDAD: ' || COUNT(*) AS RESULTADO FROM TIPOS_ACTIVIDAD
UNION ALL
SELECT 'BITACORA_CULTIVO: ' || COUNT(*) FROM BITACORA_CULTIVO;

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================

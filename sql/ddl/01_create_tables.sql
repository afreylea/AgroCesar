-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL: 01_create_tables.sql
--  Motor: Oracle XE 18c / 21c
--  Versión: 2.0
--  Cambios respecto a v1.0:
--    · Campo CATEGORIA en CULTIVOS_CATALOGO (requerido por plan v2.0)
--    · V_CULTIVOS_CON_UMBRALES ahora expone CATEGORIA y filtra por
--      usuarios activos (necesario para el motor de alertas)
--    · V_ALERTAS_ACTIVAS incluye CATEGORIA para el dashboard
--    · Índice IDX_ALERTAS_FECHA_PRON sobre FECHA_DIA_PRONOSTICO
--      (columna del UNIQUE anti-duplicación, usada en el motor)
--    · Comentarios de columna revisados y completados
--  Notas generales:
--    · Soft delete en todas las tablas (campo ACTIVO NUMBER(1))
--    · FKs con ON DELETE RESTRICT (comportamiento por defecto en Oracle)
--    · Índices en columnas de búsqueda frecuente
--    · CHECK constraints para validar rangos numéricos
-- ============================================================


-- ============================================================
--  0. LIMPIEZA (ejecutar solo en entorno de desarrollo)
-- ============================================================
BEGIN
  FOR t IN (SELECT table_name FROM user_tables
            WHERE table_name IN (
              'ALERTAS','CULTIVOS_AGRICULTOR','CULTIVOS_CATALOGO',
              'MUNICIPIOS','USUARIOS'
            ))
  LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
  END LOOP;
END;
/

-- ============================================================
--  NOTA: Con GENERATED ALWAYS AS IDENTITY las secuencias
--  las gestiona Oracle internamente. No se crean secuencias
--  ni triggers de PK manualmente.
-- ============================================================


-- ============================================================
--  1. MUNICIPIOS
--     Municipios del departamento del Cesar con coordenadas
--     geográficas para consumir la API Open-Meteo.
-- ============================================================
CREATE TABLE MUNICIPIOS (
  ID             NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOMBRE         VARCHAR2(100) NOT NULL,
  DEPARTAMENTO   VARCHAR2(100) DEFAULT 'Cesar' NOT NULL,
  LATITUD        NUMBER(9,6)   NOT NULL,
  LONGITUD       NUMBER(9,6)   NOT NULL,
  ACTIVO         NUMBER(1)     DEFAULT 1 NOT NULL,
  FECHA_CREACION DATE          DEFAULT SYSDATE NOT NULL,

  CONSTRAINT UQ_MUNICIPIOS_NOMBRE UNIQUE (NOMBRE, DEPARTAMENTO),
  CONSTRAINT CK_MUNICIPIOS_ACTIVO CHECK (ACTIVO IN (0, 1))
);

CREATE INDEX IDX_MUNICIPIOS_ACTIVO ON MUNICIPIOS(ACTIVO);


-- ============================================================
--  2. USUARIOS
--     Agricultores y administradores del sistema.
--     MUNICIPIO_ID es informativo (residencia); el motor de
--     alertas NUNCA lo usa — siempre usa el municipio del cultivo.
-- ============================================================
CREATE TABLE USUARIOS (
  ID             NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOMBRE         VARCHAR2(150) NOT NULL,
  EMAIL          VARCHAR2(255) NOT NULL,
  PASSWORD_HASH  VARCHAR2(255) NOT NULL,   -- BCrypt $2a$12$...
  ROL            VARCHAR2(20)  NOT NULL,
  MUNICIPIO_ID   NUMBER(10),               -- FK opcional; solo residencia del agricultor
  TELEFONO       VARCHAR2(20),             -- Contacto para notificaciones futuras
  ACTIVO         NUMBER(1)     DEFAULT 1 NOT NULL,
  FECHA_CREACION DATE          DEFAULT SYSDATE NOT NULL,
  ULTIMO_LOGIN   DATE,

  CONSTRAINT UQ_USUARIOS_EMAIL  UNIQUE (EMAIL),
  CONSTRAINT CK_USUARIOS_ROL    CHECK (ROL IN ('AGRICULTOR', 'ADMIN')),
  CONSTRAINT CK_USUARIOS_ACTIVO CHECK (ACTIVO IN (0, 1)),
  CONSTRAINT FK_USUARIOS_MUNICIPIO FOREIGN KEY (MUNICIPIO_ID) REFERENCES MUNICIPIOS(ID)
);

CREATE INDEX IDX_USUARIOS_EMAIL  ON USUARIOS(EMAIL);
CREATE INDEX IDX_USUARIOS_ROL    ON USUARIOS(ROL);
CREATE INDEX IDX_USUARIOS_ACTIVO ON USUARIOS(ACTIVO);


-- ============================================================
--  3. CULTIVOS_CATALOGO
--     Catálogo base de cultivos con umbrales climáticos y
--     clasificación por ciclo de vida (CATEGORIA).
--     Solo el ADMIN puede crear/editar/desactivar entradas.
-- ============================================================
CREATE TABLE CULTIVOS_CATALOGO (
  ID                  NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOMBRE              VARCHAR2(100) NOT NULL,
  DESCRIPCION         VARCHAR2(500),
  -- ★ NUEVO en v2.0: ciclo de vida del cultivo.
  --   TRANSITORIO: severidad basada en días restantes a cosecha.
  --   PERMANENTE : severidad basada en antigüedad de la planta.
  CATEGORIA           VARCHAR2(20)  DEFAULT 'TRANSITORIO' NOT NULL,
  TEMP_MIN            NUMBER(5,2)   NOT NULL,  -- °C mínima tolerable
  TEMP_MAX            NUMBER(5,2)   NOT NULL,  -- °C máxima tolerable
  LLUVIA_MIN          NUMBER(8,2)   NOT NULL,  -- mm/día mínimo necesario
  LLUVIA_MAX          NUMBER(8,2)   NOT NULL,  -- mm/día máximo tolerable
  HUMEDAD_MIN         NUMBER(5,2)   NOT NULL,  -- % humedad relativa mínima
  HUMEDAD_MAX         NUMBER(5,2)   NOT NULL,  -- % humedad relativa máxima
  TIPO_SUELO          VARCHAR2(50)  NOT NULL,  -- Suelo de referencia agronómica
  DIAS_COSECHA_MIN    NUMBER(5)     NOT NULL,  -- Días mínimos estimados a cosecha
  DIAS_COSECHA_MAX    NUMBER(5)     NOT NULL,  -- Días máximos estimados a cosecha
  FUENTE_DATOS        VARCHAR2(100),           -- Ej: 'IDEAM/FAO', 'ICA'
  ACTIVO              NUMBER(1)     DEFAULT 1 NOT NULL,
  FECHA_CREACION      DATE          DEFAULT SYSDATE NOT NULL,
  FECHA_ACTUALIZACION DATE,

  CONSTRAINT UQ_CULTIVOS_CATALOGO_NOMBRE UNIQUE (NOMBRE),
  CONSTRAINT CK_CATALOGO_CATEGORIA      CHECK (CATEGORIA IN ('TRANSITORIO', 'PERMANENTE')),
  CONSTRAINT CK_CATALOGO_ACTIVO         CHECK (ACTIVO IN (0, 1)),
  CONSTRAINT CK_CATALOGO_TEMP           CHECK (TEMP_MIN < TEMP_MAX),
  CONSTRAINT CK_CATALOGO_TEMP_RANGO     CHECK (TEMP_MIN >= -20 AND TEMP_MAX <= 80),
  CONSTRAINT CK_CATALOGO_LLUVIA_MIN     CHECK (LLUVIA_MIN >= 0),
  CONSTRAINT CK_CATALOGO_LLUVIA_RANGO   CHECK (LLUVIA_MIN < LLUVIA_MAX),
  CONSTRAINT CK_CATALOGO_LLUVIA_MAX     CHECK (LLUVIA_MAX > 0),
  CONSTRAINT CK_CATALOGO_HUMEDAD_MIN    CHECK (HUMEDAD_MIN BETWEEN 0 AND 100),
  CONSTRAINT CK_CATALOGO_HUMEDAD_RANGO  CHECK (HUMEDAD_MIN < HUMEDAD_MAX),
  CONSTRAINT CK_CATALOGO_HUMEDAD_MAX    CHECK (HUMEDAD_MAX BETWEEN 0 AND 100),
  CONSTRAINT CK_CATALOGO_DIAS           CHECK (DIAS_COSECHA_MIN > 0
                                       AND DIAS_COSECHA_MIN <= DIAS_COSECHA_MAX),
  CONSTRAINT CK_CATALOGO_TIPO_SUELO CHECK (TIPO_SUELO IN (
    'Franco','Franco-arcilloso','Franco-arenoso',
    'Arcilloso','Arenoso','Limoso','Franco-limoso'
  ))
);

-- Actualiza FECHA_ACTUALIZACION automáticamente en cada UPDATE
CREATE OR REPLACE TRIGGER TRG_CATALOGO_FECHA_ACT
  BEFORE UPDATE ON CULTIVOS_CATALOGO
  FOR EACH ROW
BEGIN
  :NEW.FECHA_ACTUALIZACION := SYSDATE;
END;
/


-- ============================================================
--  4. CULTIVOS_AGRICULTOR
--     Cultivos registrados por cada agricultor.
--     MUNICIPIO_ID referencia el municipio del cultivo
--     (no del usuario), permitiendo cultivos en municipios
--     distintos para un mismo agricultor.
--     Los campos *_OVERRIDE son NULL por defecto;
--     V_CULTIVOS_CON_UMBRALES resuelve el umbral efectivo
--     mediante NVL(override, catalogo).
-- ============================================================
CREATE TABLE CULTIVOS_AGRICULTOR (
  ID              NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  USUARIO_ID      NUMBER(10)    NOT NULL,  -- FK → USUARIOS
  CATALOGO_ID     NUMBER(10)    NOT NULL,  -- FK → CULTIVOS_CATALOGO
  MUNICIPIO_ID    NUMBER(10)    NOT NULL,  -- FK → MUNICIPIOS (municipio del cultivo)
  HECTAREAS       NUMBER(10,2)  NOT NULL,
  FECHA_SIEMBRA   DATE          NOT NULL,

  -- Umbrales personalizados: NULL = heredar del catálogo.
  -- La vista V_CULTIVOS_CON_UMBRALES aplica NVL en SQL;
  -- la capa Java NUNCA debe copiar valores del catálogo aquí.
  TEMP_MIN_OVERRIDE    NUMBER(5,2),
  TEMP_MAX_OVERRIDE    NUMBER(5,2),
  LLUVIA_MIN_OVERRIDE  NUMBER(8,2),
  LLUVIA_MAX_OVERRIDE  NUMBER(8,2),
  HUMEDAD_MIN_OVERRIDE NUMBER(5,2),
  HUMEDAD_MAX_OVERRIDE NUMBER(5,2),

  -- Tipo de suelo de la parcela. NULL = no informado todavía.
  -- Reservado para el motor de recomendaciones en versiones futuras.
  TIPO_SUELO      VARCHAR2(50),

  ACTIVO          NUMBER(1)     DEFAULT 1 NOT NULL,
  FECHA_CREACION  DATE          DEFAULT SYSDATE NOT NULL,
  FECHA_ACTUALIZACION DATE,

  CONSTRAINT FK_CULTAGR_USUARIO   FOREIGN KEY (USUARIO_ID)   REFERENCES USUARIOS(ID),
  CONSTRAINT FK_CULTAGR_CATALOGO  FOREIGN KEY (CATALOGO_ID)  REFERENCES CULTIVOS_CATALOGO(ID),
  CONSTRAINT FK_CULTAGR_MUNICIPIO FOREIGN KEY (MUNICIPIO_ID) REFERENCES MUNICIPIOS(ID),
  CONSTRAINT CK_CULTAGR_ACTIVO    CHECK (ACTIVO IN (0, 1)),
  CONSTRAINT CK_CULTAGR_HECTAREAS CHECK (HECTAREAS > 0),
  -- Permite sembrar hasta mañana para absorber diferencias de zona horaria
  CONSTRAINT CK_CULTAGR_FECHA     CHECK (FECHA_SIEMBRA <= SYSDATE + 1),
  -- Si ambos override de temperatura están presentes, deben ser coherentes
  CONSTRAINT CK_CULTAGR_TEMP_OVR  CHECK (
    TEMP_MIN_OVERRIDE IS NULL OR TEMP_MAX_OVERRIDE IS NULL
    OR TEMP_MIN_OVERRIDE < TEMP_MAX_OVERRIDE
  ),
  CONSTRAINT CK_CULTAGR_LLUVIA_OVR CHECK (
    LLUVIA_MIN_OVERRIDE IS NULL OR LLUVIA_MAX_OVERRIDE IS NULL
    OR LLUVIA_MIN_OVERRIDE < LLUVIA_MAX_OVERRIDE
  ),
  CONSTRAINT CK_CULTAGR_HUMEDAD_OVR CHECK (
    HUMEDAD_MIN_OVERRIDE IS NULL OR HUMEDAD_MAX_OVERRIDE IS NULL
    OR HUMEDAD_MIN_OVERRIDE < HUMEDAD_MAX_OVERRIDE
  ),
  -- Mismo vocabulario controlado que CULTIVOS_CATALOGO.TIPO_SUELO
  -- El CHECK en Oracle ignora NULLs, por lo que TIPO_SUELO nullable es válido
  CONSTRAINT CK_CULTAGR_TIPO_SUELO CHECK (TIPO_SUELO IN (
    'Franco','Franco-arcilloso','Franco-arenoso',
    'Arcilloso','Arenoso','Limoso','Franco-limoso'
  ))
);

-- Actualiza FECHA_ACTUALIZACION automáticamente en cada UPDATE
CREATE OR REPLACE TRIGGER TRG_CULTAGR_FECHA_ACT
  BEFORE UPDATE ON CULTIVOS_AGRICULTOR
  FOR EACH ROW
BEGIN
  :NEW.FECHA_ACTUALIZACION := SYSDATE;
END;
/

CREATE INDEX IDX_CULTAGR_USUARIO   ON CULTIVOS_AGRICULTOR(USUARIO_ID);
CREATE INDEX IDX_CULTAGR_MUNICIPIO ON CULTIVOS_AGRICULTOR(MUNICIPIO_ID);
CREATE INDEX IDX_CULTAGR_ACTIVO    ON CULTIVOS_AGRICULTOR(ACTIVO);


-- ============================================================
--  5. ALERTAS
--     Alertas generadas automáticamente por el motor.
--     Cada registro = una condición climática violada
--     en un día de pronóstico para un cultivo.
--     El UNIQUE anti-duplicación impide insertar la misma
--     combinación (cultivo, tipo, día) más de una vez.
-- ============================================================
CREATE TABLE ALERTAS (
  ID                    NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  CULTIVO_AGRICULTOR_ID NUMBER(10)    NOT NULL,  -- FK → CULTIVOS_AGRICULTOR
  TIPO_ALERTA           VARCHAR2(30)  NOT NULL,
  SEVERIDAD             VARCHAR2(10)  NOT NULL,
  DESCRIPCION           VARCHAR2(500) NOT NULL,
  FECHA_DIA_PRONOSTICO  DATE          NOT NULL,  -- Día del pronóstico que disparó la alerta
  FECHA_GENERACION      DATE          DEFAULT SYSDATE NOT NULL,
  VALOR_DETECTADO       NUMBER(8,2)   NOT NULL,  -- Valor real del pronóstico (ej: 92.5 mm)
  VALOR_UMBRAL          NUMBER(8,2)   NOT NULL,  -- Umbral efectivo aplicado (ej: 80 mm)
  LEIDA                 NUMBER(1)     DEFAULT 0 NOT NULL,
  FECHA_LECTURA         DATE,

  CONSTRAINT FK_ALERTAS_CULTIVO FOREIGN KEY (CULTIVO_AGRICULTOR_ID)
    REFERENCES CULTIVOS_AGRICULTOR(ID),
  CONSTRAINT CK_ALERTAS_TIPO CHECK (TIPO_ALERTA IN (
    'TEMPERATURA_ALTA', 'TEMPERATURA_BAJA',
    'LLUVIA_EXCESIVA',  'LLUVIA_INSUFICIENTE',
    'HUMEDAD_EXCESIVA', 'HUMEDAD_INSUFICIENTE'
  )),
  CONSTRAINT CK_ALERTAS_SEVERIDAD CHECK (SEVERIDAD IN ('ALTA','MEDIA','BAJA')),
  CONSTRAINT CK_ALERTAS_LEIDA     CHECK (LEIDA IN (0, 1)),
  -- Anti-duplicación: un tipo de alerta solo una vez por cultivo por día de pronóstico
  CONSTRAINT UQ_ALERTAS_NODUP UNIQUE (
    CULTIVO_AGRICULTOR_ID, TIPO_ALERTA, FECHA_DIA_PRONOSTICO
  )
);

-- Registra automáticamente cuándo se marcó como leída
CREATE OR REPLACE TRIGGER TRG_ALERTAS_FECHA_LECTURA
  BEFORE UPDATE OF LEIDA ON ALERTAS
  FOR EACH ROW
  WHEN (NEW.LEIDA = 1 AND OLD.LEIDA = 0)
BEGIN
  :NEW.FECHA_LECTURA := SYSDATE;
END;
/

CREATE INDEX IDX_ALERTAS_CULTIVO    ON ALERTAS(CULTIVO_AGRICULTOR_ID);
CREATE INDEX IDX_ALERTAS_TIPO       ON ALERTAS(TIPO_ALERTA);
CREATE INDEX IDX_ALERTAS_SEVERIDAD  ON ALERTAS(SEVERIDAD);
CREATE INDEX IDX_ALERTAS_FECHA_GEN  ON ALERTAS(FECHA_GENERACION);
CREATE INDEX IDX_ALERTAS_LEIDA      ON ALERTAS(LEIDA);
-- ★ NUEVO en v2.0: índice sobre la columna del UNIQUE anti-duplicación.
--   El motor de alertas filtra por FECHA_DIA_PRONOSTICO al verificar
--   si ya existe una alerta antes de intentar el INSERT.
CREATE INDEX IDX_ALERTAS_FECHA_PRON ON ALERTAS(FECHA_DIA_PRONOSTICO);


-- ============================================================
--  6. VISTAS
-- ============================================================

-- ------------------------------------------------------------
--  V_ALERTAS_ACTIVAS
--  Alertas con contexto completo para el dashboard del
--  agricultor y el historial de administración.
--  Incluye CATEGORIA para mostrar badge en la vista de alertas.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW V_ALERTAS_ACTIVAS AS
SELECT
  a.ID                     AS ALERTA_ID,
  u.NOMBRE                 AS AGRICULTOR,
  cc.NOMBRE                AS CULTIVO,
  cc.CATEGORIA,                              -- ★ NUEVO: badge TRANSITORIO/PERMANENTE
  m.NOMBRE                 AS MUNICIPIO,
  a.TIPO_ALERTA,
  a.SEVERIDAD,
  a.DESCRIPCION,
  a.VALOR_DETECTADO,
  a.VALOR_UMBRAL,
  a.FECHA_DIA_PRONOSTICO,
  a.FECHA_GENERACION,
  a.LEIDA,
  ca.USUARIO_ID,
  -- Días restantes estimados a cosecha desde hoy
  (TRUNC(ca.FECHA_SIEMBRA)
    + ROUND((cc.DIAS_COSECHA_MIN + cc.DIAS_COSECHA_MAX) / 2))
    - TRUNC(SYSDATE)       AS DIAS_RESTANTES_COSECHA
FROM ALERTAS a
JOIN CULTIVOS_AGRICULTOR ca ON a.CULTIVO_AGRICULTOR_ID = ca.ID
JOIN USUARIOS u             ON ca.USUARIO_ID  = u.ID
JOIN CULTIVOS_CATALOGO cc   ON ca.CATALOGO_ID = cc.ID
JOIN MUNICIPIOS m           ON ca.MUNICIPIO_ID = m.ID
WHERE ca.ACTIVO = 1
  AND u.ACTIVO  = 1;


-- ------------------------------------------------------------
--  V_CULTIVOS_CON_UMBRALES
--  Vista principal del motor de alertas.
--  Expone umbrales efectivos (NVL override → catálogo),
--  CATEGORIA para la lógica de severidad diferenciada, y
--  coordenadas del municipio del CULTIVO (no del usuario).
--  Filtra cultivos inactivos Y agricultores inactivos para
--  que el motor no procese cultivos de usuarios desactivados.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW V_CULTIVOS_CON_UMBRALES AS
SELECT
  ca.ID,
  ca.USUARIO_ID,
  u.NOMBRE                                        AS AGRICULTOR,
  cc.NOMBRE                                       AS CULTIVO,
  cc.CATEGORIA,                                   -- ★ NUEVO: TRANSITORIO / PERMANENTE
  m.ID                                            AS MUNICIPIO_ID,
  m.NOMBRE                                        AS MUNICIPIO,
  m.LATITUD,
  m.LONGITUD,
  ca.HECTAREAS,
  ca.FECHA_SIEMBRA,
  -- Umbrales efectivos: override del agricultor si existe, catálogo si no
  NVL(ca.TEMP_MIN_OVERRIDE,    cc.TEMP_MIN)       AS TEMP_MIN_EFECTIVA,
  NVL(ca.TEMP_MAX_OVERRIDE,    cc.TEMP_MAX)       AS TEMP_MAX_EFECTIVA,
  NVL(ca.LLUVIA_MIN_OVERRIDE,  cc.LLUVIA_MIN)     AS LLUVIA_MIN_EFECTIVA,
  NVL(ca.LLUVIA_MAX_OVERRIDE,  cc.LLUVIA_MAX)     AS LLUVIA_MAX_EFECTIVA,
  NVL(ca.HUMEDAD_MIN_OVERRIDE, cc.HUMEDAD_MIN)    AS HUMEDAD_MIN_EFECTIVA,
  NVL(ca.HUMEDAD_MAX_OVERRIDE, cc.HUMEDAD_MAX)    AS HUMEDAD_MAX_EFECTIVA,
  cc.DIAS_COSECHA_MIN,
  cc.DIAS_COSECHA_MAX,
  ROUND((cc.DIAS_COSECHA_MIN + cc.DIAS_COSECHA_MAX) / 2) AS DIAS_COSECHA_PROM,
  -- Días restantes estimados (puede ser negativo si ya pasó la cosecha)
  (TRUNC(ca.FECHA_SIEMBRA)
    + ROUND((cc.DIAS_COSECHA_MIN + cc.DIAS_COSECHA_MAX) / 2))
    - TRUNC(SYSDATE)                              AS DIAS_RESTANTES,
  ca.ACTIVO
FROM CULTIVOS_AGRICULTOR ca
JOIN USUARIOS u           ON ca.USUARIO_ID  = u.ID
JOIN CULTIVOS_CATALOGO cc ON ca.CATALOGO_ID = cc.ID
JOIN MUNICIPIOS m         ON ca.MUNICIPIO_ID = m.ID
-- ★ NUEVO: excluir cultivos de agricultores inactivos.
--   Sin este filtro el motor procesaría cultivos de usuarios
--   desactivados por el admin, generando alertas huérfanas.
WHERE u.ACTIVO = 1;


-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
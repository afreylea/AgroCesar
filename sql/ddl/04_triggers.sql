-- ============================================================
--  AGROCESAR — Sistema de Monitoreo de Cultivos
--  Script DDL: 04_triggers.sql
--  Motor: Oracle XE 18c / 21c
-- ============================================================


-- ============================================================
--  Municipios
-- ============================================================

create or replace trigger trg_municipios_pk before
   insert on municipios
   for each row
begin
   :new.id := seq_municipios.nextval;
end;
/


-- ============================================================
--  Usuarios
-- ============================================================

create or replace trigger trg_usuarios_pk before
   insert on usuarios
   for each row
begin
   :new.id := seq_usuarios.nextval;
end;
/


-- ============================================================
-- Cuntivos_Catalogo
-- ============================================================

create or replace trigger trg_cultivos_catalogo_pk before
   insert on cultivos_catalogo
   for each row
begin
   :new.id := seq_cultivos_catalogo.nextval;
end;
/

create or replace trigger trg_catalogo_fecha_act before
   update on cultivos_catalogo
   for each row
begin
   :new.fecha_actualizacion := sysdate;
end;
/


-- ============================================================
--  Cultivos_Agricultor
-- ============================================================

create or replace trigger trg_cultivos_agricultor_pk before
   insert on cultivos_agricultor
   for each row
begin
   :new.id := seq_cultivos_agricultor.nextval;
end;
/

create or replace trigger trg_cultagr_fecha_act before
   update on cultivos_agricultor
   for each row
begin
   :new.fecha_actualizacion := sysdate;
end;
/

create or replace trigger trg_valida_fecha_siembra before
   insert or update of fecha_siembra on cultivos_agricultor
   for each row
begin
   if :new.fecha_siembra > trunc(sysdate) then
      raise_application_error(
         -20010,
         'La fecha de siembra ('
         || to_char(
            :new.fecha_siembra,
            'DD/MM/YYYY'
         )
         || ') no puede ser una fecha futura. Fecha maxima permitida: '
         || to_char(
            trunc(sysdate),
            'DD/MM/YYYY'
         )
      );
   end if;
end;
/

create or replace trigger trg_catalogo_desactivar_cascade after
   update of activo on cultivos_catalogo
   for each row
   when ( new.activo = 0
      and old.activo = 1 )
begin
   update cultivos_agricultor
      set
      activo = 0
    where catalogo_id = :new.id
      and activo = 1;
end;
/


-- ============================================================
--  Alertas
-- ============================================================

create or replace trigger trg_alertas_pk before
   insert on alertas
   for each row
begin
   :new.id := seq_alertas.nextval;
end;
/

create or replace trigger trg_alertas_fecha_lectura before
   update of leida on alertas
   for each row
   when ( new.leida = 1
      and old.leida = 0 )
begin
   :new.fecha_lectura := sysdate;
end;
/
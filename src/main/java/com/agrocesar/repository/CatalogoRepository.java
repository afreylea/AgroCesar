package com.agrocesar.repository;

import com.agrocesar.model.CultivoCatalogo;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(CultivoCatalogoMapper.class)
public interface CatalogoRepository {

    @SqlQuery("SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA, TEMP_MIN, TEMP_MAX, " +
            "LLUVIA_MIN, LLUVIA_MAX, HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO, " +
            "DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO, " +
            "IMAGEN_URL, FECHA_CREACION, FECHA_ACTUALIZACION " +
            "FROM CULTIVOS_CATALOGO WHERE ID = :id")
    Optional<CultivoCatalogo> findById(@Bind("id") Long id);

    @SqlQuery("SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA, TEMP_MIN, TEMP_MAX, " +
            "LLUVIA_MIN, LLUVIA_MAX, HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO, " +
            "DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO, " +
            "IMAGEN_URL, FECHA_CREACION, FECHA_ACTUALIZACION " +
            "FROM CULTIVOS_CATALOGO WHERE ACTIVO = 1 ORDER BY NOMBRE")
    List<CultivoCatalogo> findAllActivos();

    @SqlQuery("SELECT ID, NOMBRE, DESCRIPCION, CATEGORIA, TEMP_MIN, TEMP_MAX, " +
            "LLUVIA_MIN, LLUVIA_MAX, HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO, " +
            "DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, ACTIVO, " +
            "IMAGEN_URL, FECHA_CREACION, FECHA_ACTUALIZACION " +
            "FROM CULTIVOS_CATALOGO ORDER BY NOMBRE")
    List<CultivoCatalogo> findAll();

    @SqlQuery("SELECT SEQ_CATALOGO.NEXTVAL FROM DUAL")
    Long nextId();

    @SqlUpdate("INSERT INTO CULTIVOS_CATALOGO " +
            "(ID, NOMBRE, DESCRIPCION, CATEGORIA, TEMP_MIN, TEMP_MAX, " +
            "LLUVIA_MIN, LLUVIA_MAX, HUMEDAD_MIN, HUMEDAD_MAX, TIPO_SUELO, " +
            "DIAS_COSECHA_MIN, DIAS_COSECHA_MAX, FUENTE_DATOS, IMAGEN_URL) " +
            "VALUES (:id, :nombre, :descripcion, :categoria, :tempMin, :tempMax, " +
            ":lluviaMin, :lluviaMax, :humedadMin, :humedadMax, :tipoSuelo, " +
            ":diasCosechaMin, :diasCosechaMax, :fuenteDatos, :imagenUrl)")
    void insert(@BindBean CultivoCatalogo catalogo);

    @SqlUpdate("UPDATE CULTIVOS_CATALOGO SET " +
            "NOMBRE = :nombre, DESCRIPCION = :descripcion, CATEGORIA = :categoria, " +
            "TEMP_MIN = :tempMin, TEMP_MAX = :tempMax, " +
            "LLUVIA_MIN = :lluviaMin, LLUVIA_MAX = :lluviaMax, " +
            "HUMEDAD_MIN = :humedadMin, HUMEDAD_MAX = :humedadMax, " +
            "TIPO_SUELO = :tipoSuelo, DIAS_COSECHA_MIN = :diasCosechaMin, " +
            "DIAS_COSECHA_MAX = :diasCosechaMax, FUENTE_DATOS = :fuenteDatos, " +
            "IMAGEN_URL = :imagenUrl " +
            "WHERE ID = :id")
    int update(@BindBean CultivoCatalogo catalogo);

    @SqlUpdate("UPDATE CULTIVOS_CATALOGO SET ACTIVO = 0 WHERE ID = :id")
    int desactivar(@Bind("id") Long id);

    @SqlUpdate("UPDATE CULTIVOS_CATALOGO SET ACTIVO = 1 WHERE ID = :id")
    int activar(@Bind("id") Long id);
}
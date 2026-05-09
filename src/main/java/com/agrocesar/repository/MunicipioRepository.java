package com.agrocesar.repository;

import com.agrocesar.model.Municipio;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(MunicipioMapper.class)
public interface MunicipioRepository {

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo, fecha_creacion " +
            "FROM municipios WHERE id = :id")
    Optional<Municipio> findById(@Bind("id") Long id);

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo, fecha_creacion " +
            "FROM municipios WHERE activo = 1 ORDER BY nombre")
    List<Municipio> findAllActivos();

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo, fecha_creacion " +
            "FROM municipios WHERE UPPER(nombre) LIKE UPPER(:nombre) AND activo = 1")
    List<Municipio> searchByNombre(@Bind("nombre") String nombre);

    @SqlQuery("SELECT SEQ_MUNICIPIOS.NEXTVAL FROM DUAL")
    Long nextId();

    @SqlUpdate("INSERT INTO municipios (ID, nombre, departamento, latitud, longitud) " +
           "VALUES (:id, :nombre, :departamento, :latitud, :longitud)")
    void insert(@BindBean Municipio municipio);

    @SqlUpdate("UPDATE municipios SET activo = 0 WHERE id = :id")
    int desactivar(@Bind("id") Long id);
}
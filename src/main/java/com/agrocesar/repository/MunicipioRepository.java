package com.agrocesar.repository;

import com.agrocesar.model.Municipio;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(MunicipioMapper.class)
public interface MunicipioRepository {

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo " +
            "FROM municipios WHERE id = :id")
    Optional<Municipio> findById(@Bind("id") Long id);

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo " +
            "FROM municipios WHERE activo = 1 ORDER BY nombre")
    List<Municipio> findAllActivos();

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo " +
            "FROM municipios WHERE departamento = :departamento AND activo = 1 ORDER BY nombre")
    List<Municipio> findByDepartamento(@Bind("departamento") String departamento);

    @SqlQuery("SELECT id, nombre, departamento, latitud, longitud, activo " +
            "FROM municipios WHERE UPPER(nombre) LIKE UPPER(:nombre) AND activo = 1")
    List<Municipio> searchByNombre(@Bind("nombre") String nombre);

    @SqlUpdate("INSERT INTO municipios (nombre, departamento, latitud, longitud, activo) " +
            "VALUES (:nombre, :departamento, :latitud, :longitud, 1)")
    @GetGeneratedKeys("id")
    Long insert(@BindBean Municipio municipio);

    @SqlUpdate("UPDATE municipios SET activo = 0 WHERE id = :id")
    int desactivar(@Bind("id") Long id);
}
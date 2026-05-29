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

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        WHERE ID = :id
        """)
    Optional<Municipio> findById(@Bind("id") Long id);

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        WHERE UPPER(NOMBRE) LIKE :nombre
        """)
    List<Municipio> findByNombre(@Bind("nombre") String nombre);

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        WHERE UPPER(DEPARTAMENTO) = :departamento
        ORDER BY NOMBRE
        """)
    List<Municipio> findByDepartamento(
            @Bind("departamento") String departamento
    );

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        WHERE ACTIVO = 1
        ORDER BY NOMBRE
        """)
    List<Municipio> findActivos();

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        WHERE ACTIVO = 0
        ORDER BY NOMBRE
        """)
    List<Municipio> findInactivos();

    @SqlQuery("""
        SELECT ID, NOMBRE, DEPARTAMENTO, LATITUD, LONGITUD,
               ACTIVO, FECHA_CREACION
        FROM MUNICIPIOS
        ORDER BY NOMBRE
        """)
    List<Municipio> findAll();

    @SqlUpdate("""
        INSERT INTO MUNICIPIOS (
            NOMBRE,
            DEPARTAMENTO,
            LATITUD,
            LONGITUD,
            ACTIVO,
            FECHA_CREACION
        )
        VALUES (
            :nombre,
            :departamento,
            :latitud,
            :longitud,
            :activo,
            :fechaCreacion
        )
        """)
    void insert(@BindBean Municipio municipio);

    @SqlUpdate("""
        UPDATE MUNICIPIOS
        SET NOMBRE = :nombre,
            DEPARTAMENTO = :departamento,
            LATITUD = :latitud,
            LONGITUD = :longitud,
            ACTIVO = :activo
        WHERE ID = :id
        """)
    int update(@BindBean Municipio municipio);

    @SqlUpdate("""
        UPDATE MUNICIPIOS
        SET ACTIVO = 0
        WHERE ID = :id
        """)
    int desactivar(@Bind("id") Long id);

    @SqlUpdate("""
        UPDATE MUNICIPIOS
        SET ACTIVO = 1
        WHERE ID = :id
        """)
    int activar(@Bind("id") Long id);
}
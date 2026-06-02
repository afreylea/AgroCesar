package com.agrocesar.repository;

import com.agrocesar.model.Municipio;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.OutParameters;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class MunicipioRepository {

    private final Jdbi jdbi;
    private final MunicipioMapper mapper = new MunicipioMapper();

    public MunicipioRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }
    
    // ----------------------------------------------------------------
    //  CONSULTAS
    // ----------------------------------------------------------------

    public Optional<Municipio> findById(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_by_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<Municipio>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<Municipio>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public List<Municipio> findByNombre(String nombre) {
        // nombre debe llegar con wildcards desde el servicio: UPPER('%VALLEDUPAR%')
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_by_nombre(:p_nombre, :p_cursor) }")
                .bind("p_nombre", nombre.toUpperCase())
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Municipio>>) out -> {
                    List<Municipio> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Municipio> findByDepartamento(String departamento) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_by_departamento(:p_departamento, :p_cursor) }")
                .bind("p_departamento", departamento.toUpperCase())
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Municipio>>) out -> {
                    List<Municipio> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Municipio> findActivos() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_activos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Municipio>>) out -> {
                    List<Municipio> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Municipio> findInactivos() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_inactivos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Municipio>>) out -> {
                    List<Municipio> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Municipio> findAll() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_MUNICIPIOS.prc_find_all(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Municipio>>) out -> {
                    List<Municipio> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    // ----------------------------------------------------------------
    //  ESCRITURA
    // ----------------------------------------------------------------

    public void insert(Municipio municipio) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_MUNICIPIOS.prc_insert(:p_nombre, :p_departamento, " +
                ":p_latitud, :p_longitud, :p_activo) }")
                .bind("p_nombre",       municipio.getNombre())
                .bind("p_departamento", municipio.getDepartamento())
                .bind("p_latitud",      municipio.getLatitud())
                .bind("p_longitud",     municipio.getLongitud())
                .bind("p_activo",       municipio.getActivo())
                .invoke()
        );
    }

    public int update(Municipio municipio) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_MUNICIPIOS.prc_update(:p_id, :p_nombre, :p_departamento, " +
                ":p_latitud, :p_longitud, :p_activo, :p_rows_updated) }")
                .bind("p_id",           municipio.getId())
                .bind("p_nombre",       municipio.getNombre())
                .bind("p_departamento", municipio.getDepartamento())
                .bind("p_latitud",      municipio.getLatitud())
                .bind("p_longitud",     municipio.getLongitud())
                .bind("p_activo",       municipio.getActivo())
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int desactivar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_MUNICIPIOS.prc_desactivar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int activar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_MUNICIPIOS.prc_activar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }
}
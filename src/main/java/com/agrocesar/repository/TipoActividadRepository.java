package com.agrocesar.repository;

import com.agrocesar.model.TipoActividad;
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
public class TipoActividadRepository {

    private final Jdbi jdbi;
    private final TipoActividadMapper mapper = new TipoActividadMapper();

    public TipoActividadRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<TipoActividad> listarActivos() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_TIPOS_ACTIVIDAD.listar_activos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<TipoActividad>>) out -> {
                    List<TipoActividad> list = new ArrayList<>();
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

    public Optional<TipoActividad> buscarPorId(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_TIPOS_ACTIVIDAD.buscar_por_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<TipoActividad>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<TipoActividad>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public void insertar(String nombre, String icono) {
        jdbi.useHandle(handle ->
            handle.createCall("{ call PKG_TIPOS_ACTIVIDAD.insertar(:p_nombre, :p_icono) }")
                .bind("p_nombre", nombre)
                .bind("p_icono",  icono)
                .invoke()
        );
    }

    public int cambiarEstado(Long id, int activo) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_TIPOS_ACTIVIDAD.cambiar_estado(:p_id, :p_estado, :p_rows_updated) }")
                .bind("p_id",     id)
                .bind("p_estado", activo)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }
}
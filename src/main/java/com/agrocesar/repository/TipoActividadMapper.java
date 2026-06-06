package com.agrocesar.repository;

import com.agrocesar.model.TipoActividad;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoActividadMapper implements RowMapper<TipoActividad> {

    @Override
    public TipoActividad map(ResultSet rs, StatementContext ctx) throws SQLException {
        return TipoActividad.builder()
                .id(rs.getLong("ID"))
                .nombre(rs.getString("NOMBRE"))
                .icono(rs.getString("ICONO"))
                .activo(rs.getInt("ACTIVO"))
                .fechaCreacion(rs.getDate("FECHA_CREACION").toLocalDate())
                .build();
    }
}

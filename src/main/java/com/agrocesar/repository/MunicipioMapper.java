package com.agrocesar.repository;

import com.agrocesar.model.Municipio;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MunicipioMapper implements RowMapper<Municipio> {

    @Override
    public Municipio map(ResultSet rs, StatementContext ctx) throws SQLException {

        // Oracle DATE -> Timestamp -> LocalDateTime
        Timestamp fechaCreacion = rs.getTimestamp("FECHA_CREACION");

        return Municipio.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .nombre(rs.getString("NOMBRE"))
            .departamento(rs.getString("DEPARTAMENTO"))
            .latitud(rs.getDouble("LATITUD"))
            .longitud(rs.getDouble("LONGITUD"))
            .activo(rs.getInt("ACTIVO"))
            .fechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null)

            .build();
    }
}
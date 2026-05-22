package com.agrocesar.repository;

import com.agrocesar.model.CultivoCatalogo;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CultivoCatalogoMapper implements RowMapper<CultivoCatalogo> {

    @Override
    public CultivoCatalogo map(ResultSet rs, StatementContext ctx) throws SQLException {

        // Oracle DATE -> Timestamp -> LocalDateTime
        Timestamp fechaCreacion      = rs.getTimestamp("FECHA_CREACION");
        Timestamp fechaActualizacion = rs.getTimestamp("FECHA_ACTUALIZACION");

        return CultivoCatalogo.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .nombre(rs.getString("NOMBRE"))
            .categoria(rs.getString("CATEGORIA"))
            .tempMin(rs.getDouble("TEMP_MIN"))
            .tempMax(rs.getDouble("TEMP_MAX"))
            .lluviaMin(rs.getDouble("LLUVIA_MIN"))
            .lluviaMax(rs.getDouble("LLUVIA_MAX"))
            .humedadMin(rs.getDouble("HUMEDAD_MIN"))
            .humedadMax(rs.getDouble("HUMEDAD_MAX"))
            .tipoSuelo(rs.getString("TIPO_SUELO"))
            .diasCosechaMin(rs.getInt("DIAS_COSECHA_MIN"))
            .diasCosechaMax(rs.getInt("DIAS_COSECHA_MAX"))
            .activo(rs.getInt("ACTIVO"))
            .fechaCreacion(fechaCreacion.toLocalDateTime())

            // NULLABLE
            .descripcion(rs.getString("DESCRIPCION"))
            .fuenteDatos(rs.getString("FUENTE_DATOS"))
            .imagenUrl(rs.getString("IMAGEN_URL"))
            .fechaActualizacion(fechaActualizacion != null ? fechaActualizacion.toLocalDateTime() : null)

            .build();
    }
}
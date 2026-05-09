package com.agrocesar.repository;

import com.agrocesar.model.CultivoAgricultor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CultivoAgricultorMapper implements RowMapper<CultivoAgricultor> {

    @Override
    public CultivoAgricultor map(ResultSet rs, StatementContext ctx) throws SQLException {

        // Oracle DATE sin hora -> java.sql.Date -> LocalDate
        Date fechaSiembra = rs.getDate("FECHA_SIEMBRA");

        // Oracle DATE con hora -> Timestamp -> LocalDateTime
        Timestamp fechaCreacion      = rs.getTimestamp("FECHA_CREACION");
        Timestamp fechaActualizacion = rs.getTimestamp("FECHA_ACTUALIZACION");

        return CultivoAgricultor.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .usuarioId(rs.getLong("USUARIO_ID"))
            .catalogoId(rs.getLong("CATALOGO_ID"))
            .municipioId(rs.getLong("MUNICIPIO_ID"))
            .hectareas(rs.getDouble("HECTAREAS"))
            .fechaSiembra(fechaSiembra.toLocalDate())
            .activo(rs.getInt("ACTIVO"))
            .fechaCreacion(fechaCreacion.toLocalDateTime())

            // NULLABLE — overrides de umbrales; NULL = heredar del catálogo
            .tempMinOverride(rs.getObject("TEMP_MIN_OVERRIDE", Double.class))
            .tempMaxOverride(rs.getObject("TEMP_MAX_OVERRIDE", Double.class))
            .lluviaMinOverride(rs.getObject("LLUVIA_MIN_OVERRIDE", Double.class))
            .lluviaMaxOverride(rs.getObject("LLUVIA_MAX_OVERRIDE", Double.class))
            .humedadMinOverride(rs.getObject("HUMEDAD_MIN_OVERRIDE", Double.class))
            .humedadMaxOverride(rs.getObject("HUMEDAD_MAX_OVERRIDE", Double.class))
            .latitudCultivo(rs.getObject("LATITUD_CULTIVO", Double.class))
            .longitudCultivo(rs.getObject("LONGITUD_CULTIVO", Double.class))

            // NULLABLE — tipo de suelo de la parcela
            .tipoSuelo(rs.getString("TIPO_SUELO"))

            // NULLABLE — Oracle actualiza con trigger TRG_CULTAGR_FECHA_ACT
            .fechaActualizacion(fechaActualizacion != null ? fechaActualizacion.toLocalDateTime() : null)

            .build();
    }
}
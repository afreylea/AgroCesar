package com.agrocesar.repository;

import com.agrocesar.model.Alerta;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AlertaMapper implements RowMapper<Alerta> {

    @Override
    public Alerta map(ResultSet rs, StatementContext ctx) throws SQLException {

        // Oracle DATE sin hora -> java.sql.Date -> LocalDate
        Date fechaDiaPronostico = rs.getDate("FECHA_DIA_PRONOSTICO");

        // Oracle DATE con hora -> Timestamp -> LocalDateTime
        Timestamp fechaGeneracion = rs.getTimestamp("FECHA_GENERACION");
        Timestamp fechaLectura    = rs.getTimestamp("FECHA_LECTURA");

        return Alerta.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .cultivoAgricultorId(rs.getLong("CULTIVO_AGRICULTOR_ID"))
            .tipoAlerta(rs.getString("TIPO_ALERTA"))
            .severidad(rs.getString("SEVERIDAD"))
            .descripcion(rs.getString("DESCRIPCION"))
            .fechaDiaPronostico(fechaDiaPronostico.toLocalDate())
            .fechaGeneracion(fechaGeneracion.toLocalDateTime())
            .valorDetectado(rs.getDouble("VALOR_DETECTADO"))
            .valorUmbral(rs.getDouble("VALOR_UMBRAL"))
            .recomendacion(rs.getString("RECOMENDACION"))
            .leida(rs.getInt("LEIDA"))

            // NULLABLE — Oracle lo registra con trigger TRG_ALERTAS_FECHA_LECTURA
            .fechaLectura(fechaLectura != null ? fechaLectura.toLocalDateTime() : null)

            .build();
    }
}
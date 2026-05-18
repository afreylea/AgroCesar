package com.agrocesar.repository;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CultivoConUmbralesDTOMapper implements RowMapper<CultivoConUmbralesDTO> {
    @Override
    public CultivoConUmbralesDTO map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new CultivoConUmbralesDTO(
            rs.getLong("ID"),
            rs.getLong("USUARIO_ID"),
            rs.getString("AGRICULTOR"),
            rs.getString("CULTIVO"),
            rs.getString("CATEGORIA"),
            rs.getLong("MUNICIPIO_ID"),
            rs.getString("MUNICIPIO"),
            rs.getDouble("LATITUD"),
            rs.getDouble("LONGITUD"),
            rs.getDouble("HECTAREAS"),
            rs.getTimestamp("FECHA_SIEMBRA").toLocalDateTime().toLocalDate(),
            rs.getDouble("TEMP_MIN_EFECTIVA"),
            rs.getDouble("TEMP_MAX_EFECTIVA"),
            rs.getDouble("LLUVIA_MIN_EFECTIVA"),
            rs.getDouble("LLUVIA_MAX_EFECTIVA"),
            rs.getDouble("HUMEDAD_MIN_EFECTIVA"),
            rs.getDouble("HUMEDAD_MAX_EFECTIVA"),
            rs.getInt("DIAS_COSECHA_PROM"),
            rs.getInt("DIAS_RESTANTES")
        );
    }
}
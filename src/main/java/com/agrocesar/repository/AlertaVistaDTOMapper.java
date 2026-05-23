package com.agrocesar.repository;

import com.agrocesar.dto.AlertaVistaDTO;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AlertaVistaDTOMapper implements RowMapper<AlertaVistaDTO> {

    @Override
    public AlertaVistaDTO map(ResultSet rs, StatementContext ctx) throws SQLException {

        return new AlertaVistaDTO(
            rs.getLong("ALERTA_ID"),
            rs.getString("AGRICULTOR"),
            rs.getString("CULTIVO"),
            rs.getString("CATEGORIA"),
            rs.getString("MUNICIPIO"),
            rs.getString("TIPO_ALERTA"),
            rs.getString("SEVERIDAD"),
            rs.getString("DESCRIPCION"),
            rs.getString("RECOMENDACION"),           
            rs.getDouble("VALOR_DETECTADO"),
            rs.getDouble("VALOR_UMBRAL"),
            rs.getTimestamp("FECHA_DIA_PRONOSTICO").toLocalDateTime().toLocalDate(),
            rs.getTimestamp("FECHA_GENERACION").toLocalDateTime(),
            rs.getInt("LEIDA"),
            rs.getLong("USUARIO_ID"),
            rs.getObject("DIAS_RESTANTES_COSECHA", Integer.class)
        );
    }
}
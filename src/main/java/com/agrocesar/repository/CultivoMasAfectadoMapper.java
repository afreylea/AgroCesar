package com.agrocesar.repository;

import com.agrocesar.dto.CultivoMasAfectadoDTO;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CultivoMasAfectadoMapper implements RowMapper<CultivoMasAfectadoDTO> {

    @Override
    public CultivoMasAfectadoDTO map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new CultivoMasAfectadoDTO(
                rs.getString("NOMBRECULTIVO"),
                rs.getString("MUNICIPIO"),
                rs.getInt("TOTALALERTAS")
        );
    }
}
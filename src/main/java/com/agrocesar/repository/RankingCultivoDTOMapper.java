package com.agrocesar.repository;

import com.agrocesar.dto.RankingCultivoDTO;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RankingCultivoDTOMapper implements RowMapper<RankingCultivoDTO> {

    @Override
    public RankingCultivoDTO map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new RankingCultivoDTO(
                rs.getString("NOMBRE"),
                rs.getInt("TOTAL_AGRICULTORES"),
                rs.getDouble("TOTAL_HECTAREAS"),
                rs.getString("MUNICIPIO_PRINCIPAL"));
    }
}
package com.agrocesar.repository;

import com.agrocesar.model.BitacoraCultivo;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BitacoraCultivoMapper implements RowMapper<BitacoraCultivo> {

    @Override
    public BitacoraCultivo map(ResultSet rs, StatementContext ctx) throws SQLException {

        // ALERTA_ID y DESCRIPCION son nullable
        long alertaIdRaw = rs.getLong("ALERTA_ID");
        Long alertaId = rs.wasNull() ? null : alertaIdRaw;

        return BitacoraCultivo.builder()
                .id(rs.getLong("ID"))
                .cultivoAgricultorId(rs.getLong("CULTIVO_AGRICULTOR_ID"))
                .tipoActividadId(rs.getLong("TIPO_ACTIVIDAD_ID"))
                .tipoNombre(rs.getString("TIPO_NOMBRE"))
                .tipoIcono(rs.getString("TIPO_ICONO"))
                .alertaId(alertaId)
                .descripcion(rs.getString("DESCRIPCION"))
                .fechaActividad(rs.getDate("FECHA_ACTIVIDAD").toLocalDate())
                .fechaCreacion(rs.getDate("FECHA_CREACION").toLocalDate())
                .cultivo(rs.getString("CULTIVO"))
                .build();
    }
}

package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UsuarioMapper implements RowMapper<Usuario> {

    @Override
    public Usuario map(ResultSet rs, StatementContext ctx) throws SQLException {

        // Oracle DATE -> Timestamp -> LocalDateTime
        Timestamp fechaCreacion = rs.getTimestamp("FECHA_CREACION");
        Timestamp ultimoLogin   = rs.getTimestamp("ULTIMO_LOGIN");

        return Usuario.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .nombre(rs.getString("NOMBRE"))
            .email(rs.getString("EMAIL"))
            .passwordHash(rs.getString("PASSWORD_HASH"))
            .rol(rs.getString("ROL"))
            .activo(rs.getInt("ACTIVO"))
            .fechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null)

            // NULLABLE
            .municipioId(rs.getObject("MUNICIPIO_ID", Long.class))
            .telefono(rs.getString("TELEFONO"))
            .ultimoLogin(ultimoLogin != null ? ultimoLogin.toLocalDateTime() : null)

            .build();
    }
}
package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UsuarioMapper implements RowMapper<Usuario> {
    
    @Override
    public Usuario map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Usuario.builder()
            // NOT NULL garantizado por DDL
            .id(rs.getLong("ID"))
            .nombre(rs.getString("NOMBRE"))
            .email(rs.getString("EMAIL"))
            .passwordHash(rs.getString("PASSWORD_HASH"))
            .rol(rs.getString("ROL"))
            .activo(rs.getInt("ACTIVO"))
            
            // NULLABLE → getObject()
            .municipioId(rs.getObject("MUNICIPIO_ID", Long.class))
            .telefono(rs.getString("TELEFONO"))  // VARCHAR2 NULL → null string
            .fechaCreacion(rs.getObject("FECHA_CREACION", LocalDateTime.class))
            .ultimoLogin(rs.getObject("ULTIMO_LOGIN", LocalDateTime.class))
            
            .build();
    }
}
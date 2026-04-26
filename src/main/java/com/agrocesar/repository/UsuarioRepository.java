package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

@RegisterRowMapper(UsuarioMapper.class)
public interface UsuarioRepository {

    @SqlQuery("""
        SELECT ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
               MUNICIPIO_ID, TELEFONO, ACTIVO, 
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS 
        WHERE EMAIL = :email AND ACTIVO = 1
        """)
    Optional<Usuario> findByEmailAndActivo(@Bind("email") String email);

    @SqlUpdate("""
            UPDATE USUARIOS SET ULTIMO_LOGIN = SYSDATE
            WHERE EMAIL = :email AND ACTIVO = 1
            """)
    void actualizarUltimoLogin(@Bind("email") String email);
}
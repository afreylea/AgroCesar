package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.OutParameters;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RegisterRowMapper(UsuarioMapper.class)
public interface UsuarioRepository {

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
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

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        WHERE ID = :id
        """)
    Optional<Usuario> findById(@Bind("id") Long id);

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        WHERE EMAIL = :email
        """)
    Optional<Usuario> findByEmail(@Bind("email") String email);

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        WHERE ACTIVO = 1
        ORDER BY NOMBRE
        """)
    List<Usuario> findActivos();

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        WHERE ACTIVO = 0
        ORDER BY NOMBRE
        """)
    List<Usuario> findInactivos();

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        ORDER BY NOMBRE
        """)
    List<Usuario> findAll();

    @SqlQuery("""
        SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
               MUNICIPIO_ID, TELEFONO, ACTIVO,
               FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS
        WHERE ROL = :rol AND ACTIVO = 1
        ORDER BY NOMBRE
        """)
    List<Usuario> findByRol(@Bind("rol") String rol);

    @SqlUpdate("""
        INSERT INTO USUARIOS (NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL, 
                              MUNICIPIO_ID, TELEFONO)
        VALUES (:nombre, :apellido, :email, :passwordHash, :rol, 
                :municipioId, :telefono)
        """)
    void insert(@BindBean Usuario usuario);

    @SqlUpdate("""
        UPDATE USUARIOS SET NOMBRE = :nombre, APELLIDO = :apellido, EMAIL = :email,
                            MUNICIPIO_ID = :municipioId, TELEFONO = :telefono
        WHERE ID = :id
        """)
    int update(@BindBean Usuario usuario);

    @SqlUpdate("UPDATE USUARIOS SET ACTIVO = 0 WHERE ID = :id")
    int desactivar(@Bind("id") Long id);

    @SqlUpdate("UPDATE USUARIOS SET ACTIVO = 1 WHERE ID = :id")
    int activar(@Bind("id") Long id);

    @SqlUpdate("""
    UPDATE USUARIOS SET 
        RESET_TOKEN = :token,
        RESET_TOKEN_EXPIRY = :expiry
    WHERE EMAIL = :email AND ACTIVO = 1
    """)
    int guardarResetToken(@Bind("email") String email,
                          @Bind("token") String token,
                          @Bind("expiry") java.time.LocalDateTime expiry);

    @SqlQuery("""
    SELECT ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ROL,
           MUNICIPIO_ID, TELEFONO, ACTIVO,
           FECHA_CREACION, ULTIMO_LOGIN
    FROM USUARIOS
    WHERE RESET_TOKEN = :token
      AND RESET_TOKEN_EXPIRY > SYSDATE
      AND ACTIVO = 1
    """)
    Optional<Usuario> findByResetToken(@Bind("token") String token);

    @SqlUpdate("""
    UPDATE USUARIOS SET 
        PASSWORD_HASH = :passwordHash,
        RESET_TOKEN = NULL,
        RESET_TOKEN_EXPIRY = NULL
    WHERE ID = :id
    """)
    int actualizarPassword(@Bind("id") Long id,
                           @Bind("passwordHash") String passwordHash);
}
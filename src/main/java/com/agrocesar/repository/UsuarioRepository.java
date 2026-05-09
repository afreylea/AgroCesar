package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
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

    @SqlQuery("""
        SELECT ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
                MUNICIPIO_ID, TELEFONO, ACTIVO, 
                FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS 
        WHERE ID = :id
        """)
    Optional<Usuario> findById(@Bind("id") Long id);

    @SqlQuery("""
        SELECT ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
                MUNICIPIO_ID, TELEFONO, ACTIVO, 
                FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS 
        WHERE EMAIL = :email
        """)
    Optional<Usuario> findByEmail(@Bind("email") String email);

    @SqlQuery("""
        SELECT ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
                MUNICIPIO_ID, TELEFONO, ACTIVO, 
                FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS 
        WHERE ACTIVO = 1 
        ORDER BY NOMBRE
        """)
    List<Usuario> findAllActivos();

    @SqlQuery("""
        SELECT ID, NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
                MUNICIPIO_ID, TELEFONO, ACTIVO, 
                FECHA_CREACION, ULTIMO_LOGIN
        FROM USUARIOS 
        WHERE ROL = :rol AND ACTIVO = 1 
        ORDER BY NOMBRE
        """)
    List<Usuario> findByRol(@Bind("rol") String rol);

    @SqlUpdate("""
        INSERT INTO USUARIOS (NOMBRE, EMAIL, PASSWORD_HASH, ROL, 
                              MUNICIPIO_ID, TELEFONO)
        VALUES (:nombre, :email, :passwordHash, :rol, 
                :municipioId, :telefono)
        """)
    void insert(@BindBean Usuario usuario);

    @SqlUpdate("""
        UPDATE USUARIOS SET NOMBRE = :nombre, EMAIL = :email, 
                            MUNICIPIO_ID = :municipioId 
        WHERE ID = :id
        """)
    int update(@BindBean Usuario usuario);

    @SqlUpdate("UPDATE USUARIOS SET ACTIVO = 0 WHERE ID = :id")
    int desactivar(@Bind("id") Long id);
}
